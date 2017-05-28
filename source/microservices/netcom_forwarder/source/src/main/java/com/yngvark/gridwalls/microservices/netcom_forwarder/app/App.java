package com.yngvark.gridwalls.microservices.netcom_forwarder.app;

import com.yngvark.gridwalls.microservices.netcom_forwarder.file_io.FileConsumer;
import com.yngvark.gridwalls.microservices.netcom_forwarder.file_io.FileOpener;
import com.yngvark.gridwalls.microservices.netcom_forwarder.file_io.FileWriter;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class App {
    private final ExecutorService executorService;
    private final FileOpener fileOpener;
    private final FileConsumer fileConsumer;

    private NetworkToFileHub networkToFileHub;

    public static App create(
            ExecutorService executorService,
            FileOpener fileOpener,
            FileConsumer fileConsumer) {
        return new App(
                executorService,
                fileOpener,
                fileConsumer);
    }

    App(
            ExecutorService executorService,
            FileOpener fileOpener,
            FileConsumer fileConsumer) {
        this.executorService = executorService;
        this.fileOpener = fileOpener;
        this.fileConsumer = fileConsumer;
    }

    public void run() throws Throwable {
        System.out.println("Starting network forwarder.");

        FileWriter fileWriter = fileOpener.openStream();
        networkToFileHub = new NetworkToFileHub(fileWriter);

        Future consumeNetworkFuture = consumeNetworkMessages(networkToFileHub);
        Future fileConsumer = consumeFileMessages();

        Future allFutures = executorService.submit(() -> {
            try {
                System.out.println("Waiting for consumeNetworkFuture to return.");
                consumeNetworkFuture.get();
                System.out.println("Waiting, with timeout, for fileConsumer to return.");
                fileConsumer.get(3, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }
        });

        System.out.println("Waiting for allFutures to return...");
        allFutures.get();
        System.out.println("Waiting for allFutures to return... done.");

        fileWriter.closeStream();
    }

    private Future consumeNetworkMessages(NetworkToFileHub networkToFileHub) {
        return executorService.submit(() -> {
                try {
                    networkToFileHub.consumeAndForward();
                    fileConsumer.stopConsuming();
                } catch (IOException|InterruptedException e) {
                    System.out.println("Exception occurred");
                    e.printStackTrace();
                }
            });
    }

    private Future consumeFileMessages() throws IOException {
        return executorService.submit(() -> {
            try {
                fileConsumer.consume();
                networkToFileHub.stop();
            } catch (IOException e) {
                System.out.println("Exception occurred");
                e.printStackTrace();
            }
        });
    }

    public void stop() {
        System.out.println("Stopping app.");

        if (networkToFileHub != null)
            networkToFileHub.stop();
    }
}
