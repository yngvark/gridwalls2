package com.yngvark.gridwalls.microservices.netcom_forwarder;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        if (args.length != 2)
            System.err.println("USAGE: <this program> <mkfifo input> <mkfifo output>");

        ExecutorService executorService = Executors.newCachedThreadPool();

        String fifoInputFilename = args[0];
        String fifoOutputFilename = args[1];

        System.out.println("Start forwarder");

        Future producerFuture = executorService.submit(() -> {
            NetcomProducer producer = new NetcomProducer();
            try {
                producer.produce(fifoOutputFilename);
            } catch (IOException|InterruptedException e) {
                e.printStackTrace();
            }
        });

        NetcomConsumer consumer = new NetcomConsumer();
        consumer.consume(fifoInputFilename);

        producerFuture.get(); // Wait for producer to finish.

        executorService.shutdown();
        executorService.shutdownNow();
    }
}
