package com.akka.learn;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.util.concurrent.CompletionStage;

import akka.stream.*;
import akka.stream.javadsl.*;
import akka.util.ByteString;

public class Main {

    public static void main(String[] args) throws IOException {


        //final ActorSystem actorSystem = ActorSystem.create();


        /**
         * Actors
         */
//        final ActorRef akkaBot = actorSystem.actorOf(Props.create(AkkaBot.class),"akkaBot");
//
//        akkaBot.tell(
//                new AkkaBot.Move(AkkaBot.Direction.FORWARD),
//                ActorRef.noSender());
//        akkaBot.tell(
//                new AkkaBot.Move(AkkaBot.Direction.BACKWARDS),
//                ActorRef.noSender());
//        akkaBot.tell(
//                new AkkaBot.Stop(),
//                ActorRef.noSender());


//        final ActorRef botMaster = actorSystem.actorOf(
//                Props.create(BotMaster.class),
//                "botMaster");
//
//        botMaster.tell(new BotMaster.StartChildBots(), ActorRef.noSender());

        /**
         * Streams
         */

        final ActorSystem system = ActorSystem.create("QuickStart");

        /**
         *  The Materializer is a factory for stream execution engines,
         *  it is the thing that makes streams run—you don’t need to worry about any of the details
         *  right now apart from that you need one for calling any of the run methods on a Source.
         */
        final Materializer materializer = ActorMaterializer.create(system);

        final Source<Integer, NotUsed> source = Source.range(1, 100);
        //final CompletionStage<Done> done = source.runForeach(i -> System.out.println(i), materializer);
        //done.thenRun(() -> system.terminate());


        /**
         *
         First we use the scan operator to run a computation over the whole stream:
         starting with the number 1 (BigInteger.ONE) we multiple by each of the incoming numbers,
         one after the other; the scan operation emits the initial value and then every calculation result.
         This yields the series of factorial numbers which we stash away as a Source for later reuse—
         it is important to keep in mind that nothing is actually computed yet, this is a description of
         what we want to have computed once we run the stream.
         */
        final Source<BigInteger, NotUsed> factorials = source
                .scan(BigInteger.ONE, (acc, next) -> {
                    System.out.println("ACC : " + acc + " NEXT : " + next);
                    return acc.multiply(BigInteger.valueOf(next));
                });

        //final CompletionStage<Done> done = factorials.runForeach(i -> System.out.println(i), materializer);


        /**
         * Then we convert the resulting series of numbers into a stream of ByteString objects describing lines in a text file.
         * This stream is then run by attaching a file as the receiver of the data. In the terminology of Akka Streams
         * this is called a Sink. IOResult is a type that IO operations return in Akka Streams
         * in order to tell you how many bytes or elements were consumed and whether the stream terminated
         * normally or exceptionally.
         */
//        final CompletionStage<IOResult> result =
//                factorials
//                        .map(num -> ByteString.fromString(num.toString() + "\n"))
//                        .runWith(FileIO.toPath(Paths.get("factorials.txt")), materializer);


        factorials.map(BigInteger::toString).runWith(lineSink("factorial2.txt"), materializer);


    }

    /**
     * Every stream processing stage can produce a materialized value which can be captured using viaMat or toMat
     * (as opposed to via() or to(), respectively). In your code snippet, the using of source.toMat(sink) indicates
     * that you're interested in capturing the materialized value of the source and sink and Keep.right keeps
     * the right side (i.e. sink) of the materialized value.  Keep.left would keep the materialized value on the
     * left side (i.e. source), and Keep.both would allow you to keep both.
     * @param filename
     * @return
     */
    public static Sink<String, CompletionStage<IOResult>> lineSink(String filename) {
        return Flow.of(String.class)
                .map(s -> ByteString.fromString(s + "\n"))
                .toMat(FileIO.toPath(Paths.get(filename)), Keep.right());
    }
}
