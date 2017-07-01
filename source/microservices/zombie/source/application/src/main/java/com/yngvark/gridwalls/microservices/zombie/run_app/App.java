package com.yngvark.gridwalls.microservices.zombie.run_app;

import com.yngvark.communicate_through_named_pipes.input.InputFileReader;
import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.gridwalls.microservices.zombie.run_game.GameFactory;
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
    private final InputFileReader inputFileReader;
    private final OutputFileWriter outputFileWriter;
    private final NetworkMessageReceiver networkMessageReceiver;
    private final GameEventProducer eventProducer;

    public static App create(
            ExecutorService executorService,
            InputFileReader netcomReader,
            OutputFileWriter netcomWriter
    ) {
        GameFactory gameFactory = GameFactory.create();

        return new App(
                executorService,
                netcomReader,
                netcomWriter,
                new NetworkMessageReceiver(
                        gameFactory.createNetworkMessageListener()
                ),
                gameFactory.createEventProducer(netcomWriter)
                );
    }

    public App(ExecutorService executorService,
            InputFileReader inputFileReader, OutputFileWriter outputFileWriter,
            NetworkMessageReceiver networkMessageReceiver,
            GameEventProducer eventProducer) {
        this.executorService = executorService;
        this.inputFileReader = inputFileReader;
        this.outputFileWriter = outputFileWriter;
        this.networkMessageReceiver = networkMessageReceiver;
        this.eventProducer = eventProducer;
    }

    public void run() throws Throwable {
        Future netcomConsumerFuture = startConsumeEvents();
        Future netcomProducerFuture = startProduceEvents();

        Future allFutures = executorService.submit(() -> {
            try {
                logger.info("Waiting for producerFuture to return...");
                netcomProducerFuture.get();
                logger.info("Waiting for producerFuture to return... done.");

                logger.info("Waiting, with timeout, for netcomConsumerFuture to return...");
                netcomConsumerFuture.get(3, TimeUnit.SECONDS);
                logger.info("Waiting, with timeout, for netcomConsumerFuture to return... done.");
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }
        });

        logger.info("Waiting for allFutures to return...");
        allFutures.get();
        logger.info("Waiting for allFutures to return... done.");

        outputFileWriter.closeStream();
    }

    private Future startConsumeEvents() throws IOException {
        return executorService.submit(() -> {
            try {
                inputFileReader.consume(networkMessageReceiver);
                eventProducer.stop();
            } catch (IOException e) {
                logger.info("Exception occurred");
                throw new RuntimeException(e);
            }
        });
    }

    private Future startProduceEvents() {
        return executorService.submit(() -> {
            eventProducer.produce();
            inputFileReader.closeStream(); // TODO should throw ioexception?
        });
    }

    public void stop() {
        logger.info("Stopping app.");
        if (eventProducer == null)
            return;

        eventProducer.stop();
    }
}
