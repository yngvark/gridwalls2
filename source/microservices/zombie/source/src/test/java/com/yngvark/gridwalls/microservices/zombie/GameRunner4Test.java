package com.yngvark.gridwalls.microservices.zombie;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.*;

public class GameRunner4Test {

    public static void main(String[] args) {
        new GameRunner4Test().run();
    }
    private Future systemInReaderFuture;

    public void run() {
        ExecutorService executorService = Executors.newCachedThreadPool();
//        Runtime.getRuntime().addShutdownHook(new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                System.out.println("Running shutdownhook.");
//                executorService.shutdownNow();
//                System.out.println("Running shutdownhook... done.");
//            }
//        });

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        systemInReaderFuture = executorService.submit(() -> {
            System.out.println("Enter input:");
            try {
                String a = bufferedReader.readLine();
                System.out.println("Input: " + a);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Thread done.");

        });

        try {
            systemInReaderFuture.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        executorService.shutdown();
        System.out.println("Quitting");
    }

}