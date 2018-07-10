package com.akka.learn.kafka;

import akka.Done;
import akka.actor.ActorSystem;
import akka.kafka.ProducerSettings;
import akka.kafka.javadsl.Producer;
import akka.stream.Materializer;
import akka.stream.javadsl.Source;
import com.typesafe.config.Config;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Random;
import java.util.concurrent.CompletionStage;

public class AkkaProducer {

    private static Config config;
    private static ProducerSettings<String, String> producerSettings;
    private static Materializer materializer;

    public static void init(ActorSystem system, Materializer mat) {
        config = system.settings().config().getConfig("akka.kafka.producer");
        producerSettings = ProducerSettings
                .create(config, new StringSerializer(), new StringSerializer())
                .withBootstrapServers("192.168.99.100:9092");
        materializer = mat;
    }

    public static void sendMessages(String topic, String uuid) {
        CompletionStage<Done> done =
                Source.range(1, 3)
                        .map(number -> number.toString())
                        .map(value -> new ProducerRecord<String, String>(topic, uuid, generateMessage(uuid)))
                        .runWith(Producer.plainSink(producerSettings), materializer);

        done.thenRun(() -> {
            System.out.println("MESSAGES SENT");
        });
    }


    public static String generateMessage(String uuid) {
        Random random = new Random();
        int i = random.nextInt(99999999);
        return "Message-" + i + "-" + uuid;
    }
}
