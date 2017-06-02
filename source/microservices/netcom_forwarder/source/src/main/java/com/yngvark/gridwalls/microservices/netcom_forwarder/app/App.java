package com.yngvark.gridwalls.microservices.netcom_forwarder.app;

import com.yngvark.communicate_through_named_pipes.FileExistsWaiter;
import com.yngvark.communicate_through_named_pipes.RetrySleeper;
import com.yngvark.communicate_through_named_pipes.input.InputFileOpener;
import com.yngvark.communicate_through_named_pipes.input.InputFileReader;
import com.yngvark.communicate_through_named_pipes.output.OutputFileOpener;
import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.gridwalls.microservices.netcom_forwarder.app.forward_msgs.NetworkMsgListenerFactory;
import com.yngvark.gridwalls.microservices.netcom_forwarder.app.forward_msgs.NetworkToFileHub;
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
    private final FileExistsWaiter fileExistsWaiter;
    private final InputFileOpener microserviceReaderOpener;
    private final OutputFileOpener microserviceWriterOpener;
    private final NetworkToFileHub networkToFileHub;

    public static App create(
            ExecutorService executorService,
            InputFileOpener microserviceReaderOpener,
            OutputFileOpener microserviceWriterOpener) {
        RetrySleeper retrySleeper = () -> Thread.sleep(3000);
        FileExistsWaiter fileExistsWaiter = new FileExistsWaiter(retrySleeper);

        return new App(
                executorService,
                fileExistsWaiter,
                microserviceReaderOpener,
                microserviceWriterOpener,
                NetworkToFileHub.create()
                );
    }

    public App(ExecutorService executorService,
            FileExistsWaiter fileExistsWaiter,
            InputFileOpener microserviceReaderOpener,
            OutputFileOpener microserviceWriterOpener,
            NetworkToFileHub networkToFileHub) {
        this.executorService = executorService;
        this.fileExistsWaiter = fileExistsWaiter;
        this.microserviceReaderOpener = microserviceReaderOpener;
        this.microserviceWriterOpener = microserviceWriterOpener;
        this.networkToFileHub = networkToFileHub;
    }

    public void run() throws Throwable {
        logger.info("Starting network forwarder.");

        // Class for writing messages to microservices.
        OutputFileWriter microserviceWriter = microserviceWriterOpener.openStream(fileExistsWaiter);

        // Class for reading messages from microservices.
        InputFileReader microserviceReader = microserviceReaderOpener.openStream(fileExistsWaiter);

        Future consumeNetworkFuture = startConsumeNetworkMessages(microserviceWriter, microserviceReader);
        Future fileConsumer = startConsumeMessagesFromMicroservice(microserviceReader);

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

    private Future startConsumeNetworkMessages(
            OutputFileWriter microserviceWriter,
            InputFileReader microserviceReader) {
        return executorService.submit(() -> {
            try {
                networkToFileHub.consumeAndForwardTo(microserviceWriter);
                microserviceReader.closeStream();
            } catch (IOException | InterruptedException e) {
                logger.info("Exception occurred");
                e.printStackTrace();
            }
        });
    }

    private Future startConsumeMessagesFromMicroservice(InputFileReader microserviceReader) throws IOException {
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
