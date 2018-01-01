package com.yngvark.gridwalls.microservices.zombie.gameloop;

import com.yngvark.gridwalls.microservices.zombie.move_zombie.Zombie;
import com.yngvark.gridwalls.microservices.zombie.produce_events.EventProducer;
import com.yngvark.gridwalls.microservices.zombie.react_to_events.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class GameLoopRunner {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final BufferedReader bufferedReader;
    private final EventHandler eventHandler;
    private final EventProducer eventProducer;
    private final Sleeper sleeper;

    private boolean run = true;

    public GameLoopRunner(
            BufferedReader bufferedReader,
            EventHandler eventHandler,
            EventProducer eventProducer,
            Sleeper sleeper
    ) {
        this.bufferedReader = bufferedReader;
        this.eventHandler = eventHandler;
        this.eventProducer = eventProducer;
        this.sleeper = sleeper;
    }

    public static GameLoopRunner create(
            BufferedReader bufferedReader,
            BufferedWriter bufferedWriter,
            Sleeper sleeper,
            Zombie zombie) {

        return new GameLoopRunner(
                bufferedReader,
                EventHandler.create(zombie),
                EventProducer.create(
                        zombie,
                        bufferedWriter),
                sleeper
        );
    }

    public void run() {
        logger.info("--- STARTING GAME ---------------------------------------------------------");

        while (run) {
            runOneIteration();
        }
    }

    void runOneIteration() {
        LocalDateTime timeBeforeReadingMessagesFromNetwork = LocalDateTime.now();

        while (existsMessageFromNetwork()) {
            String incomingEvent = readIncomingEvent();
            eventHandler.handle(incomingEvent);
        }

        eventProducer.produce();
        waitRemainderOfTurn(timeBeforeReadingMessagesFromNetwork);
    }

    private boolean existsMessageFromNetwork() {
        boolean result;
        try {
            result = bufferedReader.ready();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        logger.trace("existsMessageFromNetwork: {}", result);
        return result;
    }

    private String readIncomingEvent() {
        logger.debug("Reading incoming event... ");

        String result;
        try {
            result = bufferedReader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        logger.debug("Reading incoming event... {}", result);
        return result;
    }

    private void waitRemainderOfTurn(LocalDateTime beforeReadingMessagesFromNetwork) {
        Duration duration = Duration.between(beforeReadingMessagesFromNetwork, LocalDateTime.now());
        long millisToWait = 1000 - duration.toMillis();
        sleeper.sleep(TimeUnit.MILLISECONDS, millisToWait);
    }

    public void stop() {
        run = false;
    }
}
