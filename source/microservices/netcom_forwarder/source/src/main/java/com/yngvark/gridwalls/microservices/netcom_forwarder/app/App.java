package com.yngvark.gridwalls.microservices.netcom_forwarder.app;

import com.yngvark.communicate_through_named_pipes.input.InputFileOpener;
import com.yngvark.communicate_through_named_pipes.input.InputFileReader;
import com.yngvark.communicate_through_named_pipes.output.OutputFileOpener;
import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq.RabbitBrokerConnecter;
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
    private final InputFileOpener microserviceReaderOpener;
    private final OutputFileOpener microserviceWriterOpener;

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
            InputFileOpener microserviceReaderOpener,
            OutputFileOpener microserviceWriterOpener) {
        this.executorService = executorService;
        this.microserviceReaderOpener = microserviceReaderOpener;
        this.microserviceWriterOpener = microserviceWriterOpener;
    }

    RabbitBrokerConnecter rabbitBrokerConnecter;
    public void run() throws Throwable {
        logger.info("Starting network forwarder.");

        OutputFileWriter microserviceWriter = microserviceWriterOpener.openStream();
        networkToFileHub = new NetworkToFileHub(microserviceWriter);
        InputFileReader microserviceReader = microserviceReaderOpener.openStream();

        Future consumeNetworkFuture = startConsumeNetworkMessages(networkToFileHub, microserviceReader);
        Future fileConsumer = startConsumeMessagesFromMicroservice(microserviceReader, networkToFileHub);

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

        microserviceWriter.closeStream();
    }

    private Future startConsumeNetworkMessages(NetworkToFileHub networkToFileHub, InputFileReader inputFileReader) {
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

    private Future startConsumeMessagesFromMicroservice(
            InputFileReader microserviceReader,
            NetworkToFileHub networkToFileHub
    ) throws IOException
    {
        return executorService.submit(() -> {
            try {
                microserviceReader.consume(new MicroserviceMsgListener());
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
