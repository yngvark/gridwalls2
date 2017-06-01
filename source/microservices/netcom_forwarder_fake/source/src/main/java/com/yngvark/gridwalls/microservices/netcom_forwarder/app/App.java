package com.yngvark.gridwalls.microservices.netcom_forwarder.app;

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

    private InputFileReader inputFileReader;

    private NetworkToFileHub networkToFileHub;

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
        logger.info("Starting network forwarder.");

        OutputFileWriter outputFileWriter = outputFileOpener.openStream();
        networkToFileHub = new NetworkToFileHub(outputFileWriter);

        inputFileReader = inputFileOpener.openStream();
        Future consumeNetworkFuture = consumeNetworkMessages(networkToFileHub);
        Future fileConsumer = consumeFileMessages();

        Future allFutures = executorService.submit(() -> {
            try {
                logger.info("Waiting for consumeNetworkFuture to return.");
                consumeNetworkFuture.get();
                logger.info("Waiting, with timeout, for fileConsumer to return.");
                fileConsumer.get(3, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }
        });

        logger.info("Waiting for allFutures to return...");
        allFutures.get();
        logger.info("Waiting for allFutures to return... done.");

        outputFileWriter.closeStream();
    }

    private Future consumeNetworkMessages(NetworkToFileHub networkToFileHub) {
        return executorService.submit(() -> {
                try {
                    networkToFileHub.consumeAndForward();
                    inputFileReader.closeStream();
                } catch (IOException|InterruptedException e) {
                    logger.info("Exception occurred");
                    e.printStackTrace();
                }
            });
    }

    private Future consumeFileMessages() throws IOException {
        return executorService.submit(() -> {
            try {
                inputFileReader.consume();
                networkToFileHub.stop();
            } catch (IOException e) {
                logger.info("Exception occurred");
                e.printStackTrace();
            }
        });
    }

    public void stop() {
        logger.info("Stopping app.");

        if (networkToFileHub != null)
            networkToFileHub.stop();
    }
}
