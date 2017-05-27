package com.yngvark.gridwalls.microservices.zombie2.app;

import com.yngvark.gridwalls.microservices.zombie2.netcom.NetcomConsumer;
import com.yngvark.gridwalls.microservices.zombie2.netcom.NetcomSender;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class App {
    private final ExecutorService executorService;
    private final NetcomSender netcomSender;
    private final NetcomConsumer netcomConsumer;
    private final Game game;

    public static App create(
            ExecutorService executorService,
            NetcomSender netcomSender,
            NetcomConsumer netcomConsumer) {
        return new App(
                executorService,
                netcomSender,
                netcomConsumer,
                new Game(netcomSender));
    }

    App(
            ExecutorService executorService,
            NetcomSender netcomSender,
            NetcomConsumer netcomConsumer,
            Game game) {
        this.executorService = executorService;
        this.netcomSender = netcomSender;
        this.netcomConsumer = netcomConsumer;
        this.game = game;
    }

    public void run() throws Throwable {
        /*
        Muligheter for exit:
        - Shutdownhook sier stop.
        - consumeren stopper.
        - game stopper.
         */

        System.out.println("Starting zombie logic.");

        netcomSender.openStream();

        Future netcomConsumerFuture = consumeMessages();
        Future gameFuture = runGame();

        Future allFutures = executorService.submit(() -> {
            try {
                System.out.println("Waiting for gameFuture to return.");
                gameFuture.get();
                System.out.println("Waiting, with timeout, for netcomConsumerFuture to return.");
                netcomConsumerFuture.get(3, TimeUnit.SECONDS);
                System.out.println("netcomConsumerFuture complete");
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }
        });

        System.out.println("Waiting for allFutures to return...");
        allFutures.get();
        System.out.println("allFutures complete");

        netcomSender.closeStream();
    }

    private Future runGame() {
        return executorService.submit(() -> {
                try {
                    game.produce();
                    netcomConsumer.stopConsuming();
                } catch (IOException|InterruptedException e) {
                    System.out.println("Exception occurred");
                    e.printStackTrace();
                }
            });
    }

    private Future consumeMessages() throws IOException {
        return executorService.submit(() -> {
            try {
                netcomConsumer.consume();
                game.stop();
            } catch (IOException e) {
                System.out.println("Exception occurred");
                e.printStackTrace();
            }
        });
    }

    public void stop() {
        System.out.println("Stopping app.");
        game.stop();
    }
}
