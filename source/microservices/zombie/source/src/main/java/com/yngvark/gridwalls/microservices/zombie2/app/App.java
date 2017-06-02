package com.yngvark.gridwalls.microservices.zombie2.app;

import com.yngvark.communicate_through_named_pipes.RetrySleeper;
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
    private final InputFileOpener netcomReaderOpener;
    private final OutputFileOpener netcomWriterOpener;
    private final RetrySleeper retrySleeper;

    private Game game;
    private InputFileReader netcomReader;

    public static App create(
            ExecutorService executorService,
            InputFileOpener inputFileOpener,
            OutputFileOpener outputFileOpener) {
        return new App(
                executorService,
                inputFileOpener,
                outputFileOpener,
                () -> Thread.sleep(1000));
    }

    public App(ExecutorService executorService,
            InputFileOpener netcomReaderOpener,
            OutputFileOpener netcomWriterOpener, RetrySleeper retrySleeper) {
        this.executorService = executorService;
        this.netcomReaderOpener = netcomReaderOpener;
        this.netcomWriterOpener = netcomWriterOpener;
        this.retrySleeper = retrySleeper;
    }

    public void run() throws Throwable {
        logger.info("Starting zombie logic.");

        netcomReader = netcomReaderOpener.openStream(retrySleeper);
        Future netcomConsumerFuture = startConsumeMessagesFromNetcomForwarder(netcomReader);

        OutputFileWriter netcomWriter = netcomWriterOpener.openStream(retrySleeper);
        game = new Game(netcomWriter);

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

        netcomWriter.closeStream();
    }

    private Future startConsumeMessagesFromNetcomForwarder(InputFileReader netcomReader) throws IOException {
        return executorService.submit(() -> {
            try {
                netcomReader.consume(new NetworkMessageListener());
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
                    netcomReader.closeStream();
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
