package com.yngvark;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;

public class KafkaRunner {
    public static void produce() {
        Properties props = new Properties();
        //props.put("bootstrap.servers", "localhost:9092");
        props.put("bootstrap.servers", "kafka:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(props);
        String topic = "test";
        Object response = producer.send(new ProducerRecord<String, String>(topic, "KafkaRunner", "Cows eat grass OFTEN."));

        producer.close();

        System.out.println("FutureResponse:" + response);
    }

    public static void consumeOnce() {
        Properties props = new Properties();
        //props.put("bootstrap.servers", "localhost:9092"); // Should be correct
        props.put("bootstrap.servers", "kafka:9092"); // Should be correct
        //props.put("group.id", "------------YKGROUP2-----------");
        props.put("group.id", UUID.randomUUID().toString());
        props.put("enable.auto.commit", "false");
        //props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("auto.offset.reset", "earliest");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("test"));
        //List<PartitionInfo> partitions = consumer.partitionsFor("test");
        //TopicPartition topicPartition = new TopicPartition("test", 0);
        //consumer.seekToBeginning(Collections.singleton(topicPartition));

        int tries = 0;
        boolean receivedData = false;
        ConsumerRecords<String, String> records = null;
        while (!receivedData && tries < 10) {
            tries++;

            records = consumer.poll(0);
            if (records.count() > 0) {
                receivedData = true;
                break;
            }
        }
        if (receivedData)
            System.out.println("Got data after " + tries + " tries.");
        else
            System.out.println("Got NO data after " + tries + " tries.");

        if (records != null) {
            System.out.println("Record count: " + records.count());
            for (ConsumerRecord<String, String> record : records)
                System.out.printf("offset = %d, key = %s, value = %s\n", record.offset(), record.key(), record.value());
        }
        consumer.close();
        /**
         *Record count: 9
         offset = 0, key = null, value = HEYoffset = 1, key = null, value = HOoffset = 2, key = hey, value = HAHLLHAoffset = 3, key = hey, value = HAHLLHAoffset = 4, key = hey, value = HAHLLHAoffset = 5, key = null, value = HAHAHoffset = 6, key = null, value = HEHEoffset = 7, key = null, value = hahaoffset = 8, key = null, value = hohohoh
         Process finished with exit code 0

         *
         */
        /*
        int i = 0;
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records)
                System.out.printf("offset = %d, key = %s, value = %s", record.offset(), record.key(), record.value());
            i++;
            if (1 == 1)
                break;
        }
        */
    }
    private static boolean continueListening = true;

    public static void consumeContinously() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092"); // Should be correct
        //props.put("bootstrap.servers", "kafka:9092"); // Should be correct
        //props.put("bootstrap.servers", "zookeeper:2181");
        props.put("group.id", "------------HEY2-----------");
        //props.put("group.id", UUID.randomUUID().toString());
        props.put("enable.auto.commit", "true");
        //props.put("auto.commit.interval.ms", "1000");
        //props.put("session.timeout.ms", "30000");
        props.put("auto.offset.reset", "earliest");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("test"));

        int i = 0;
        while (continueListening && i < 50) {
            System.out.println("Polling...");
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records)
                System.out.printf("offset = %d, key = %s, value = %s\n", record.offset(), record.key(), record.value());
            i++;
        }
        consumer.close();
    }
}
