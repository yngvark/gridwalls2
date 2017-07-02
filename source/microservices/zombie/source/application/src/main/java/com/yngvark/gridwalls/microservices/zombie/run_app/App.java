package com.yngvark.gridwalls.microservices.zombie.run_app;

import com.yngvark.communicate_through_named_pipes.input.InputFileReader;
import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.gridwalls.microservices.zombie.run_game.GameEventProducer;
import com.yngvark.gridwalls.microservices.zombie.run_game.GameFactory;
import org.slf4j.Logger;

import java.util.Random;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Future;

import static org.slf4j.LoggerFactory.getLogger;

public class App {
    private final Logger logger = getLogger(getClass());

    private final CompletionService completionService;
    private final InputFileReader inputFileReader;
    private final OutputFileWriter outputFileWriter;
    private final NetworkMessageReceiver networkMessageReceiver;
    private final GameEventProducer gameEventProducer;

    public static App create(
            CompletionService completionService,
            InputFileReader inputFileReader,
            OutputFileWriter outputFileWriter
    ) {
        GameFactory gameFactory = GameFactory.create(
                new ThreadSleeper(),
                new Random(),
                new JsonSerializer()
        );

        return new App(
                completionService,
                inputFileReader,
                outputFileWriter,
                new NetworkMessageReceiver(gameFactory.createNetworkMessageListener()),
                gameFactory.create(outputFileWriter)
                );
    }

    public App(CompletionService completionService,
            InputFileReader inputFileReader,
            OutputFileWriter outputFileWriter,
            NetworkMessageReceiver networkMessageReceiver,
            GameEventProducer gameEventProducer) {
        this.completionService = completionService;
        this.inputFileReader = inputFileReader;
        this.outputFileWriter = outputFileWriter;
        this.networkMessageReceiver = networkMessageReceiver;
        this.gameEventProducer = gameEventProducer;
    }

    public void run() throws Throwable {
        startConsumeEvents();
        startProduceEvents();

        // Thanks to https://stackoverflow.com/questions/19348248/waiting-on-a-list-of-future
        logger.info("Waiting for game consumer and producer to return...");
        for (int i = 0; i < 2; i++) {
            Future<String> consumeOrProduceFuture = completionService.take();
            String whichFuture = consumeOrProduceFuture.get();
            logger.info("A completion service returned: {}", whichFuture);
        }
        logger.info("Waiting for game consumer and producer to return... done.");

        outputFileWriter.closeStream();
    }

    private void startConsumeEvents() {
        completionService.submit(() -> {
            logger.debug("Consuming events...");
            inputFileReader.consume(networkMessageReceiver);
            logger.debug("Consuming events... done.");

            gameEventProducer.stop();
            return "Consumer";
        });
    }

    private void startProduceEvents() {
        completionService.submit(() -> {
            logger.debug("Producing events...");
            gameEventProducer.produce();
            logger.debug("Producing events... done.");

            inputFileReader.closeStream(); // TODO should throw ioexception?
            return "PRodycer";
        });
    }

    public void stop() {
        logger.info("Stopping app.");
        if (gameEventProducer == null)
            return;

        gameEventProducer.stop();
    }
}
