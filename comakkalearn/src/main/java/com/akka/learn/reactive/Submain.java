package com.akka.learn.reactive;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Source;

public class Submain {

    public static final Hashtag AKKA = new Hashtag("#akka");

    public static void run() {

        /**
         * The ActorMaterializer can optionally take ActorMaterializerSettings which can be used to define materialization properties,
         * such as default buffer sizes (see also Buffers for asynchronous stages), the dispatcher to be used by the pipeline etc.
         * These can be overridden with withAttributes on Flow, Source, Sink and Graph.
         */
        final ActorSystem system = ActorSystem.create("reactive-tweets");
        final Materializer mat = ActorMaterializer.create(system);

        // a stream of tweets
        Source<Tweet, NotUsed> tweets;


    }

}
