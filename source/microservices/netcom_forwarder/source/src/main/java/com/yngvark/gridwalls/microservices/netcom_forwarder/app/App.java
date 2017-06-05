package com.yngvark.gridwalls.microservices.netcom_forwarder.app;

import com.yngvark.communicate_through_named_pipes.RetrySleeper;
import com.yngvark.communicate_through_named_pipes.input.InputFileOpener;
import com.yngvark.communicate_through_named_pipes.input.InputFileReader;
import com.yngvark.communicate_through_named_pipes.output.OutputFileOpener;
import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.gridwalls.microservices.netcom_forwarder.app.consume_input_file.InputFileConsumer;
import com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq.RabbitBrokerConnecter;
import com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq.RabbitConnection;
import com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq.RabbitPublisher;
import com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq.RabbitSubscriber;
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
    private final InputFileOpener inputFileOpener;
    private final OutputFileOpener outputFileOpener;
    private final RabbitMessageListenerFactory rabbitMessageListenerFactory;

    private RabbitConnection rabbitConnection;
    private RabbitSubscriber rabbitSubscriber;
    private RabbitPublisher rabbitPublisher;

    private OutputFileWriter outputFileWriter;
    private InputFileReader inputFileReader;
    private InputFileConsumer inputFileConsumer;

    private SubscribeToListener subscribeToListener;

    private boolean stopped = false;

    public static App create(
            ExecutorService executorService,
            InputFileOpener inputFileOpener,
            OutputFileOpener outputFileOpener,
            String host
    ) {
        RetrySleeper retrySleeper = () -> Thread.sleep(3000);

        return new App(
                executorService,
                new RabbitBrokerConnecter(host),
                retrySleeper,
                inputFileOpener,
                outputFileOpener,
                new RabbitMessageListenerFactory()
        );
    }

    public App(
            ExecutorService executorService,
            RabbitBrokerConnecter rabbitBrokerConnecter,
            RetrySleeper retrySleeper, InputFileOpener inputFileOpener,
            OutputFileOpener outputFileOpener,
            RabbitMessageListenerFactory rabbitMessageListenerFactory
    ) {
        this.executorService = executorService;
        this.rabbitBrokerConnecter = rabbitBrokerConnecter;
        this.retrySleeper = retrySleeper;
        this.inputFileOpener = inputFileOpener;
        this.outputFileOpener = outputFileOpener;
        this.rabbitMessageListenerFactory = rabbitMessageListenerFactory;
    }

    public void run() throws Throwable {
        logger.info("Starting network forwarder.");
        rabbitConnection = rabbitBrokerConnecter.connect();
        rabbitSubscriber = new RabbitSubscriber(rabbitConnection);
        rabbitPublisher = new RabbitPublisher(rabbitConnection);

        outputFileWriter = outputFileOpener.openStream(retrySleeper);
        inputFileReader = inputFileOpener.openStream(retrySleeper);
        inputFileConsumer = new InputFileConsumer(inputFileReader);

        ConsumerNameListener consumerNameListener = new ConsumerNameListener();
        inputFileConsumer.addMessageListener(consumerNameListener, "/myNameIs");

        subscribeToListener = new SubscribeToListener(
                consumerNameListener,
                rabbitMessageListenerFactory,
                outputFileWriter,
                rabbitSubscriber
        );

        inputFileConsumer.addMessageListener(subscribeToListener, "/subscribeTo");
        inputFileConsumer.addMessageListener(
                new PublishListener(rabbitPublisher, consumerNameListener),
                "/publish"
        );

        Future consumeNetworkFuture = startConsumeMessagesFromNetwork(subscribeToListener, inputFileReader);
        Future consumeInputFuture = startConsumeMessagesFromInputFile(inputFileConsumer, subscribeToListener);

        Future allFutures = executorService.submit(() -> {
            try {
                logger.trace("Waiting for consumeNetworkFuture to return.");
                consumeNetworkFuture.get();

                logger.info("Waiting, with timeout, for fileConsumer to return.");
                consumeInputFuture.get(3, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        });

        logger.info("Waiting for allFutures to return...");
        allFutures.get();
        logger.info("Waiting for allFutures to return... done.");
    }

    private Future startConsumeMessagesFromNetwork(
            SubscribeToListener subscribeToListener,
            InputFileReader microserviceReader) {
        return executorService.submit(() -> {
            try {
                subscribeToListener.blockUntilStopped();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            microserviceReader.closeStream();
        });
    }

    private Future startConsumeMessagesFromInputFile(
            InputFileConsumer inputFileConsumer,
            SubscribeToListener subscribeToListener) {
        return executorService.submit(() -> {
            try {
                inputFileConsumer.consume();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            subscribeToListener.stop();
        });
    }

    public synchronized void stop() {
        logger.info("Stopping app...");
        if (stopped)
            return;
        stopped = true;

        if (outputFileWriter != null)
            outputFileWriter.closeStream();
        if (inputFileReader != null)
            inputFileReader.closeStream();
        if (subscribeToListener != null)
            subscribeToListener.stop();
        if (rabbitConnection != null)
            rabbitConnection.disconnectIfConnected();
        
        logger.info("Stopping app... done.");
        logger.info("Nothing more stop logic should happen now, except graceful shutdown, which should do nothing.");
    }
}
