package com.akka.learn;

import java.io.IOException;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import com.akka.learn.kafka.AkkaProducer;
import com.akka.learn.kafka.SimpleProducer;

public class Main {

    public static void main(String[] args) throws IOException {


        //simple producer
//        String topic = "test";
//        SimpleProducer.sendMessage(topic, SimpleProducer.uuid1);
//        SimpleProducer.sendMessage(topic, SimpleProducer.uuid2);
//        SimpleProducer.sendMessage(topic, SimpleProducer.uuid3);


        final ActorSystem system = ActorSystem.create();
        final Materializer materializer = ActorMaterializer.create(system);
        AkkaProducer.init(system, materializer);
        AkkaProducer.sendMessages("test-akka", SimpleProducer.uuid1);
        AkkaProducer.sendMessages("test-akka", SimpleProducer.uuid2);
        AkkaProducer.sendMessages("test-akka", SimpleProducer.uuid3);


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

        //final ActorSystem system = ActorSystem.create("QuickStart");

        /**
         *  The Materializer is a factory for stream execution engines,
         *  it is the thing that makes streams run—you don’t need to worry about any of the details
         *  right now apart from that you need one for calling any of the run methods on a Source.
         */
//        final Materializer materializer = ActorMaterializer.create(system);
//
//        final Source<Integer, NotUsed> source = Source.range(1, 100);
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
//        final Source<BigInteger, NotUsed> factorials = source
//                .scan(BigInteger.ONE, (acc, next) -> {
//                    //System.out.println("ACC : " + acc + " NEXT : " + next);
//                    return acc.multiply(BigInteger.valueOf(next));
//                });

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

        //reusing logic by using a flow
        //factorials.map(BigInteger::toString).runWith(lineSink("factorial2.txt"), materializer);


        /**
         * Time-Based Processing
         *
         * Starting from the factorials source we transform the stream by zipping it together with another stream,
         * represented by a Source that emits the number 0 to 100: the first number emitted by the factorials
         * source is the factorial of zero, the second is the factorial of one, and so on. We combine these
         * two by forming strings like "3! = 6".
         *
         * https://doc.akka.io/japi/akka/current/akka/stream/scaladsl/ZipWith.html
         *
         * Throttle
         *
         * Limit the throughput to a specific number of elements per time unit, or a
         * specific total cost per time unit, where a function has to be provided to calculate the individual cost of each element.
         *
         * Throttle implements the token bucket model. There is a bucket with a given token capacity (burst size or maximumBurst).
         * Tokens drops into the bucket at a
         * given rate and can be 'spared' for later use up to bucket capacity to allow some burstiness.
         * Whenever stream wants to send an element, it takes as many tokens from the bucket as number of elements.
         * If there isn't any, throttle waits until the bucket accumulates enough tokens.
         * Bucket is full when stream just materialized and started.
         *
         * throttle operator slows down the stream to 1 element per second
         */

//        factorials
//                .zipWith(Source.range(0, 99), (num, idx) -> String.format("%d! = %s", idx, num))
//                .throttle(1, Duration.ofSeconds(1))
//                .runForeach(s -> System.out.println(s), materializer);


        /**
         * Flows and Graphs
         */

//        final Source<Integer, NotUsed> source2 =
//                Source.from(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));

        //https://doc.akka.io/docs/akka/current/stream/operators/Source-or-Flow/fold.html
        //fold - Start with current value zero and then apply the current and next value to the given function,
        //when upstream complete the current value is emitted downstream.
//        final Sink<Integer, CompletionStage<Integer>> sink =
//                Sink.fold(0, (aggr, next) -> aggr + next);

        // connect the Source to the Sink, obtaining a RunnableFlow/RunnableGraph
//        final RunnableGraph<CompletionStage<Integer>> runnable =
//                source2.toMat(sink, Keep.right());

        // Materialize the flow
//        final CompletionStage<Integer> sum = runnable.run(materializer);
//        sum.thenAccept(total -> {
//            System.out.println("TOTAL SUM : " + total);
//        });


        /**
         * Source from completion stage
         */
//        Source.fromCompletionStage(new CompletableFuture<Integer>().supplyAsync(() -> {
//            return Arrays.asList(1, 2, 3);
//        }));

        // Explicitly creating and wiring up a Source, Sink and Flow
//        Source.from(Arrays.asList(1, 2, 3, 4))
//                .via(Flow.of(Integer.class).map(elem -> elem * 2))
//                .to(Sink.foreach(System.out::println));

        /**
         * There are various ways to wire up different parts of a stream, the following examples show some of
         * the available options:
         */

        // Explicitly creating and wiring up a Source, Sink and Flow
//        Source.from(Arrays.asList(1, 2, 3, 4))
//                .via(Flow.of(Integer.class).map(elem -> elem * 2))
//                .to(Sink.foreach(System.out::println));

        // Starting from a Source
//        final Source<Integer, NotUsed> source1 = Source.from(Arrays.asList(1, 2, 3, 4))
//                .map(elem -> elem * 2);
//        source1.to(Sink.foreach(System.out::println));

        // Starting from a Sink
//        final Sink<Integer, NotUsed> sink1 = Flow.of(Integer.class)
//                .map(elem -> elem * 2).to(Sink.foreach(System.out::println));
//        Source.from(Arrays.asList(1, 2, 3, 4)).to(sink1);


        /**
         * Difference between to/toMat, and via/viaMat
         *
         * https://stackoverflow.com/questions/35818358/akka-stream-tomat
         *
         * via is just a shortcut for viaMat(...)(Keep.left), and in fact this is how it's implemented:
         *
         *
         * toMat is the same as viaMat but for sinks, it lets you keep the materialized value from the
         * left (source/flow) or right (sink) side or both
         *
         * Keep.both is just an alias for (a:A,b:B) => (a, b), that is a function that takes the two input
         * parameters and return them as a tuple. It's used to have the materialized value of both left
         * and right side when combining two flows (or source and flow or flow and sink etc)
         **/

        /**
         *
         * https://doc.akka.io/japi/akka/current/akka/stream/javadsl/Source.html#actorRef-int-akka.stream.OverflowStrategy-
         *
         * Creates a Source that is materialized as an ActorRef. Messages sent to this actor will be emitted
         * to the stream if there is demand from downstream, otherwise they will be buffered until request for demand is received.
         */
//        Source<String, ActorRef> matValuePoweredSource =
//                Source.actorRef(100, OverflowStrategy.fail());
//
//        Pair<ActorRef, Source<String, NotUsed>> actorRefSourcePair =
//                matValuePoweredSource.preMaterialize(materializer);
//
//        actorRefSourcePair.first().tell("Hello!", ActorRef.noSender());

        // pass source around for materialization
        //actorRefSourcePair.second().runWith(Sink.foreach(System.out::println), materializer);


    }

    /**
     * Every stream processing stage can produce a materialized value which can be captured using viaMat or toMat
     * (as opposed to via() or to(), respectively). In your code snippet, the using of source.toMat(sink) indicates
     * that you're interested in capturing the materialized value of the source and sink and Keep.right keeps
     * the right side (i.e. sink) of the materialized value.  Keep.left would keep the materialized value on the
     * left side (i.e. source), and Keep.both would allow you to keep both.
     *
     * @param filename
     * @return
     */
//    public static Sink<String, CompletionStage<IOResult>> lineSink(String filename) {
//        return Flow.of(String.class)
//                .map(s -> ByteString.fromString(s + "\n"))
//                .toMat(FileIO.toPath(Paths.get(filename)), Keep.right());
//    }


}
