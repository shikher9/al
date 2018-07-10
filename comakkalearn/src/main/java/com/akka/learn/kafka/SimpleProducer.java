package com.akka.learn.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.Random;

public class SimpleProducer {


    private static Properties properties;
    public final static String uuid1 = "c063d095-93a3-454f-b910-b89b068b189f";
    public final static String uuid2 = "2207d142-6250-4a71-b4b3-920a165fc514";
    public final static String uuid3 = "b2e73cc3-99b6-4c8d-bbb4-3882ace6c267";

    static {
        properties = new Properties();
        properties.setProperty("bootstrap.servers", "192.168.99.100:9092");
        properties.setProperty("key.serializer", StringSerializer.class.getName());
        properties.setProperty("value.serializer", StringSerializer.class.getName());
        properties.setProperty("acks", "1");
        properties.setProperty("retries", "3");
    }


    public static void sendMessage(String topic, String uuid) {

        System.out.println("SENDING MESSAGE");

        Producer<String, String> producer = new KafkaProducer<String, String>(properties);

        //topic,key,value
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(
                topic,
                uuid,
                generateMessage(uuid));

        producer.send(producerRecord);
        producer.flush();
        producer.close();

        System.out.println("MESSAGE SENT");

    }

    public static String generateMessage(String uuid) {
        Random random = new Random();
        int i = random.nextInt(99999999);
        return "Message-" + i + "-" + uuid;
    }
}
