package com.yngvark.gridwalls.microservices.zombie2.app;

import com.yngvark.communicate_through_named_pipes.input.InputFileOpener;
import com.yngvark.communicate_through_named_pipes.input.InputFileReader;
import com.yngvark.communicate_through_named_pipes.output.OutputFileOpener;
import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.slf4j.LoggerFactory.getLogger;

public class App {
    private final Logger logger = getLogger(getClass());
    private final ExecutorService executorService;
    private final InputFileOpener inputFileOpener;
    private final OutputFileOpener outputFileOpener;

    private Game game;
    private InputFileReader inputFileReader;

    public static App create(
            ExecutorService executorService,
            InputFileOpener inputFileOpener,
            OutputFileOpener outputFileOpener) {
        return new App(
                executorService,
                inputFileOpener,
                outputFileOpener);
    }

    App(
            ExecutorService executorService,
            InputFileOpener inputFileOpener,
            OutputFileOpener outputFileOpener) {
        this.executorService = executorService;
        this.inputFileOpener = inputFileOpener;
        this.outputFileOpener = outputFileOpener;
    }

    public void run() throws Throwable {
        /*
        Muligheter for exit:
        - Shutdownhook sier stop.
        - consumeren stopper.
        - game stopper.
         */

        logger.info("Starting zombie logic.");

        inputFileReader = inputFileOpener.openStream();
        Future netcomConsumerFuture = consumeMessages(inputFileReader);

        OutputFileWriter outputFileWriter = outputFileOpener.openStream();
        game = new Game(outputFileWriter);

        Future gameFuture = runGame(game);

        Future allFutures = executorService.submit(() -> {
            try {
                logger.info("Waiting for gameFuture to return.");
                gameFuture.get();
                logger.info("Waiting, with timeout, for netcomConsumerFuture to return.");
                netcomConsumerFuture.get(3, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }
        });

        logger.info("Waiting for allFutures to return...");
        allFutures.get();
        logger.info("Waiting for allFutures to return... done.");

        outputFileWriter.closeStream();
    }

    private Future consumeMessages(InputFileReader inputFileReader) throws IOException {
        return executorService.submit(() -> {
            try {
                inputFileReader.consume();
                game.stop();
            } catch (IOException e) {
                logger.info("Exception occurred");
                e.printStackTrace();
            }
        });
    }

    private Future runGame(Game game) {
        return executorService.submit(() -> {
                try {
                    game.produce();
                    inputFileReader.closeStream();
                } catch (IOException|InterruptedException e) {
                    logger.info("Exception occurred");
                    e.printStackTrace();
                }
            });
    }

    public void stop() {
        logger.info("Stopping app.");

        if (game == null)
            throw new IllegalStateException("Cannot stop game that hasn't started.");

        game.stop();
    }
}
