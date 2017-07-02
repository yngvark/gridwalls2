package com.yngvark.gridwalls.microservices.zombie.run_app;

import com.yngvark.communicate_through_named_pipes.input.InputFileReader;
import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.gridwalls.microservices.zombie.run_game.Game;
import com.yngvark.gridwalls.microservices.zombie.run_game.GameFactory;
import org.slf4j.Logger;

import java.util.Random;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static org.slf4j.LoggerFactory.getLogger;

public class App {
    private final Logger logger = getLogger(getClass());

    private final CompletionService completionService;
    private final InputFileReader inputFileReader;
    private final OutputFileWriter outputFileWriter;
    private final NetworkMessageReceiver networkMessageReceiver;
    private final Game game;

    public static App create(
            CompletionService completionService,
            InputFileReader netcomReader,
            OutputFileWriter netcomWriter
    ) {
        GameFactory gameFactory = GameFactory.create(
                new ThreadSleeper(),
                new Random(),
                new JsonSerializer()
        );

        return new App(
                completionService,
                netcomReader,
                netcomWriter,
                new NetworkMessageReceiver(gameFactory.createNetworkMessageListener()),
                gameFactory.create(netcomWriter)
                );
    }

    public App(CompletionService completionService,
            InputFileReader inputFileReader,
            OutputFileWriter outputFileWriter,
            NetworkMessageReceiver networkMessageReceiver, Game game) {
        this.completionService = completionService;
        this.inputFileReader = inputFileReader;
        this.outputFileWriter = outputFileWriter;
        this.networkMessageReceiver = networkMessageReceiver;
        this.game = game;
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

            game.stop();
            return "Consumer";
        });
    }

    private void startProduceEvents() {
        completionService.submit(() -> {
            logger.debug("Producing events...");
            game.produce();
            logger.debug("Producing events... done.");

            inputFileReader.closeStream(); // TODO should throw ioexception?
            return "PRodycer";
        });
    }

    public void stop() {
        logger.info("Stopping app.");
        if (game == null)
            return;

        game.stop();
    }
}
