package com.yngvark.gridwalls.microservices.netcom_forwarder.app;

import com.yngvark.communicate_through_named_pipes.RetrySleeper;
import com.yngvark.communicate_through_named_pipes.input.InputFileReader;
import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.gridwalls.microservices.netcom_forwarder.app.consume_input_file.InputFileConsumer;
import com.yngvark.gridwalls.rabbitmq.RabbitBrokerConnecter;
import com.yngvark.gridwalls.rabbitmq.RabbitConnection;
import com.yngvark.gridwalls.rabbitmq.RabbitPublisher;
import com.yngvark.gridwalls.rabbitmq.RabbitSubscriber;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.slf4j.LoggerFactory.getLogger;

public class App {
    private final Logger logger = getLogger(getClass());
    private final CompletionService completionService;
    private final RabbitConnection rabbitConnection;
    private final RetrySleeper retrySleeper;
    private final InputFileReader inputFileReader;
    private final OutputFileWriter outputFileWriter;
    private final RabbitMessageListenerFactory rabbitMessageListenerFactory;

    private RabbitSubscriber rabbitSubscriber;
    private RabbitPublisher rabbitPublisher;

    private InputFileConsumer inputFileConsumer;

    private SubscribeToListener subscribeToListener;

    private boolean stopped = false;

    public static App create(
            CompletionService completionService,
            RabbitConnection rabbitConnection,
            InputFileReader inputFileReader,
            OutputFileWriter outputFileWriter
    ) {
        RetrySleeper retrySleeper = () -> Thread.sleep(3000);

        return new App(
                completionService,
                rabbitConnection,
                retrySleeper,
                inputFileReader,
                outputFileWriter,
                new RabbitMessageListenerFactory()
        );
    }

    public App(CompletionService completionService,
            RabbitConnection rabbitConnection,
            RetrySleeper retrySleeper,
            InputFileReader inputFileReader,
            OutputFileWriter outputFileWriter,
            RabbitMessageListenerFactory rabbitMessageListenerFactory) {
        this.completionService = completionService;
        this.rabbitConnection = rabbitConnection;
        this.retrySleeper = retrySleeper;
        this.inputFileReader = inputFileReader;
        this.outputFileWriter = outputFileWriter;
        this.rabbitMessageListenerFactory = rabbitMessageListenerFactory;
    }

    public void run() throws Throwable {
        logger.info("Starting network forwarder.");

        rabbitSubscriber = new RabbitSubscriber(rabbitConnection);
        rabbitPublisher = new RabbitPublisher(rabbitConnection);

        subscribeToListener = new SubscribeToListener(
                UUID.randomUUID().toString(),
                rabbitMessageListenerFactory,
                outputFileWriter,
                rabbitSubscriber
        );

        inputFileConsumer = new InputFileConsumer(inputFileReader);
        inputFileConsumer.addMessageListener(subscribeToListener, "/subscribeTo");
        inputFileConsumer.addMessageListener(
                new PublishListener(rabbitPublisher),
                "/publishTo"
        );

        startConsumeMessagesFromNetwork(subscribeToListener, inputFileReader);
        startConsumeMessagesFromInputFile(inputFileConsumer, subscribeToListener);

        // Thanks to https://stackoverflow.com/questions/19348248/waiting-on-a-list-of-future
        logger.info("Waiting for game consumer and producer to return...");
        for (int i = 0; i < 2; i++) {
            Future<String> consumeOrProduceFuture = completionService.take();
            String whichFuture = consumeOrProduceFuture.get();
            logger.info("A completion service returned: {}", whichFuture);
        }
        logger.info("Waiting for game consumer and producer to return... done.");
    }

    private void startConsumeMessagesFromNetwork(
            SubscribeToListener subscribeToListener,
            InputFileReader microserviceReader) {
        completionService.submit(() -> {
            subscribeToListener.blockUntilStopped();
            microserviceReader.closeStream();
            return "NetworkConsumer";
        });
    }

    private void startConsumeMessagesFromInputFile(
            InputFileConsumer inputFileConsumer,
            SubscribeToListener subscribeToListener) {
        completionService.submit(() -> {
            inputFileConsumer.consume();
            subscribeToListener.stop();
            return "InputFileConsumer";
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
