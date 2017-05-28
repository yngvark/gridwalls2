package com.yngvark.gridwalls.microservices.zombie2.app;

import com.yngvark.communicate_through_named_pipes.file_io.FileConsumer;
import com.yngvark.communicate_through_named_pipes.file_io.FileOpener;
import com.yngvark.communicate_through_named_pipes.file_io.FileWriter;

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

    private Game game;

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
        /*
        Muligheter for exit:
        - Shutdownhook sier stop.
        - consumeren stopper.
        - game stopper.
         */

        System.out.println("Starting zombie logic.");

        Future netcomConsumerFuture = consumeMessages();

        FileWriter fileWriter = fileOpener.openStream();
        game = new Game(fileWriter);

        Future gameFuture = runGame(game);

        Future allFutures = executorService.submit(() -> {
            try {
                System.out.println("Waiting for gameFuture to return.");
                gameFuture.get();
                System.out.println("Waiting, with timeout, for netcomConsumerFuture to return.");
                netcomConsumerFuture.get(3, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }
        });

        System.out.println("Waiting for allFutures to return...");
        allFutures.get();
        System.out.println("Waiting for allFutures to return... done.");

        fileWriter.closeStream();
    }

    private Future runGame(Game game) {
        return executorService.submit(() -> {
                try {
                    game.produce();
                    fileConsumer.stopConsuming();
                } catch (IOException|InterruptedException e) {
                    System.out.println("Exception occurred");
                    e.printStackTrace();
                }
            });
    }

    private Future consumeMessages() throws IOException {
        return executorService.submit(() -> {
            try {
                fileConsumer.consume();
                game.stop();
            } catch (IOException e) {
                System.out.println("Exception occurred");
                e.printStackTrace();
            }
        });
    }

    public void stop() {
        System.out.println("Stopping app.");

        if (game == null)
            throw new IllegalStateException("Cannot stop game that hasn't started.");

        game.stop();
    }
}
