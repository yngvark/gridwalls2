package com.yngvark.gridwalls.microservices.netcom_forwarder.app;

import com.yngvark.communicate_through_named_pipes.RetrySleeper;
import com.yngvark.communicate_through_named_pipes.input.InputFileOpener;
import com.yngvark.communicate_through_named_pipes.input.InputFileReader;
import com.yngvark.communicate_through_named_pipes.output.OutputFileOpener;
import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.gridwalls.microservices.netcom_forwarder.app.forward_msgs.NetworkToMsForwarder;
import com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq.RabbitBrokerConnecter;
import com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq.RabbitConnection;
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
    private final RabbitBrokerConnecter rabbitBrokerConnecter;
    private final RetrySleeper retrySleeper;
    private final InputFileOpener microserviceReaderOpener;
    private final OutputFileOpener microserviceWriterOpener;
    private final NetworkToMsForwarder networkToMsForwarder;

    private OutputFileWriter microserviceWriter;
    private InputFileReader microserviceReader;
    private RabbitConnection rabbitConnection;
    private boolean stopped = false;

    public static App create(
            ExecutorService executorService,
            InputFileOpener microserviceReaderOpener,
            OutputFileOpener microserviceWriterOpener) {
        RetrySleeper retrySleeper = () -> Thread.sleep(3000);

        return new App(
                executorService,
                new RabbitBrokerConnecter(),
                retrySleeper,
                microserviceReaderOpener,
                microserviceWriterOpener,
                NetworkToMsForwarder.create()
                );
    }

    public App(ExecutorService executorService,
            RabbitBrokerConnecter rabbitBrokerConnecter,
            RetrySleeper retrySleeper,
            InputFileOpener microserviceReaderOpener,
            OutputFileOpener microserviceWriterOpener,
            NetworkToMsForwarder networkToMsForwarder) {
        this.executorService = executorService;
        this.rabbitBrokerConnecter = rabbitBrokerConnecter;
        this.retrySleeper = retrySleeper;
        this.microserviceReaderOpener = microserviceReaderOpener;
        this.microserviceWriterOpener = microserviceWriterOpener;
        this.networkToMsForwarder = networkToMsForwarder;
    }

    public void run() throws Throwable {
        logger.info("Starting network forwarder.");
        rabbitConnection = rabbitBrokerConnecter.connect("rabbitmq");

        // Class for writing messages to microservices.
        microserviceWriter = microserviceWriterOpener.openStream(retrySleeper);

        // Class for reading messages from microservices.
        microserviceReader = microserviceReaderOpener.openStream(retrySleeper);

        Future consumeNetworkFuture = startConsumeNetworkMessages(rabbitConnection, microserviceWriter, microserviceReader);
        Future fileConsumerFuture = startConsumeMessagesFromMicroservice(microserviceReader);

        Future allFutures = executorService.submit(() -> {
            try {
                logger.trace("Waiting for consumeNetworkFuture to return.");
                consumeNetworkFuture.get();

                logger.info("Waiting, with timeout, for fileConsumer to return.");
                fileConsumerFuture.get(3, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        });

        logger.info("Waiting for allFutures to return...");
        allFutures.get();
        logger.info("Waiting for allFutures to return... done.");
    }

    private Future startConsumeNetworkMessages(
            RabbitConnection rabbitConnection,
            OutputFileWriter microserviceWriter,
            InputFileReader microserviceReader) {
        return executorService.submit(() -> {
            try {
                networkToMsForwarder.consumeAndForward(rabbitConnection, microserviceWriter);
                microserviceReader.closeStream();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private Future startConsumeMessagesFromMicroservice(InputFileReader microserviceReader) throws IOException {
        return executorService.submit(() -> {
            try {
                microserviceReader.consume(new MicroserviceMsgListener());
                networkToMsForwarder.stop();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public synchronized void stop() {
        logger.info("Stopping app...");
        if (stopped)
            throw new RuntimeException("Already stopped!");
        stopped = true;

        microserviceWriter.closeStream();
        microserviceReader.closeStream();
        networkToMsForwarder.stop();

        if (rabbitConnection != null)
            rabbitConnection.disconnectIfConnected();
        
        logger.info("Stopping app... done.");
        logger.info("Nothing more stop logic should happen now, except graceful shutdown, which should do nothing.");
    }
}
