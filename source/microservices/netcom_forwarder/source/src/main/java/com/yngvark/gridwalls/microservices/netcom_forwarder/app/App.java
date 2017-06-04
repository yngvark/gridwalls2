package com.yngvark.gridwalls.microservices.netcom_forwarder.app;

import com.yngvark.communicate_through_named_pipes.RetrySleeper;
import com.yngvark.communicate_through_named_pipes.input.InputFileOpener;
import com.yngvark.communicate_through_named_pipes.input.InputFileReader;
import com.yngvark.communicate_through_named_pipes.output.OutputFileOpener;
import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.gridwalls.microservices.netcom_forwarder.app.consume_msgs_from_network.Netcom;
import com.yngvark.gridwalls.microservices.netcom_forwarder.app.consume_msgs_from_ms.MicroserviceConsumer;
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
    private final NetworkToMsForwarderFactory networkToMsForwarderFactory;

    private RabbitConnection rabbitConnection;
    private OutputFileWriter microserviceWriter;
    private InputFileReader microserviceReader;
    private MicroserviceConsumer microserviceConsumer;
    private Netcom netcom;

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
                new NetworkToMsForwarderFactory()
        );
    }

    public App(ExecutorService executorService,
            RabbitBrokerConnecter rabbitBrokerConnecter,
            RetrySleeper retrySleeper,
            InputFileOpener microserviceReaderOpener,
            OutputFileOpener microserviceWriterOpener,
            NetworkToMsForwarderFactory networkToMsForwarderFactory) {
        this.executorService = executorService;
        this.rabbitBrokerConnecter = rabbitBrokerConnecter;
        this.retrySleeper = retrySleeper;
        this.microserviceReaderOpener = microserviceReaderOpener;
        this.microserviceWriterOpener = microserviceWriterOpener;
        this.networkToMsForwarderFactory = networkToMsForwarderFactory;
    }

    public void run() throws Throwable {
        logger.info("Starting network forwarder.");
        rabbitConnection = rabbitBrokerConnecter.connect("rabbitmq");

        microserviceWriter = microserviceWriterOpener.openStream(retrySleeper);
        microserviceReader = microserviceReaderOpener.openStream(retrySleeper);
        netcom = networkToMsForwarderFactory.create(rabbitConnection, microserviceWriter);
        microserviceConsumer = MicroserviceConsumer.create(rabbitConnection, microserviceReader, netcom); // TODO fix.

        Future consumeNetworkFuture = startConsumeMessagesFromNetwork(netcom, microserviceReader);
        Future fileConsumerFuture = startConsumeMessagesFromMicroservice(netcom);

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

    private Future startConsumeMessagesFromNetwork(
            Netcom netcom,
            InputFileReader microserviceReader) {
        return executorService.submit(() -> {
            try {
                netcom.blockUntilStopped();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            microserviceReader.closeStream();
        });
    }

    private Future startConsumeMessagesFromMicroservice(Netcom netcom) {
        return executorService.submit(() -> {
            try {
                microserviceConsumer.consume();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            netcom.stop();
        });
    }

    public synchronized void stop() {
        logger.info("Stopping app...");
        if (stopped)
            throw new RuntimeException("Already stopped!");
        stopped = true;

        if (microserviceWriter != null)
            microserviceWriter.closeStream();
        if (microserviceReader != null)
            microserviceReader.closeStream();
        if (netcom != null)
            netcom.stop();
        if (rabbitConnection != null)
            rabbitConnection.disconnectIfConnected();
        
        logger.info("Stopping app... done.");
        logger.info("Nothing more stop logic should happen now, except graceful shutdown, which should do nothing.");
    }
}
