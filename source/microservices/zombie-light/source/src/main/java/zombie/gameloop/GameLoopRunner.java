package zombie.gameloop;

import zombie.move_zombie.Zombie;
import zombie.move_zombie.ZombieFactory;
import zombie.produce_events.EventProducer;
import zombie.react_to_events.EventHandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class GameLoopRunner {
    private final BufferedReader bufferedReader;
    private final EventHandler eventHandler;
    private final EventProducer eventProducer;
    private final Sleeper sleeper;
    private final Random random;

    public GameLoopRunner(BufferedReader bufferedReader, EventHandler eventHandler, EventProducer eventProducer, Sleeper sleeper,
            Random random) {
        this.bufferedReader = bufferedReader;
        this.eventHandler = eventHandler;
        this.eventProducer = eventProducer;
        this.sleeper = sleeper;
        this.random = random;
    }

    public static GameLoopRunner create(BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        Zombie zombie = ZombieFactory.create();

        return new GameLoopRunner(
                bufferedReader,
                EventHandler.create(zombie),
                EventProducer.create(
                        zombie,
                        bufferedWriter),
                new ThreadSleeper(),
                new Random());
    }

    public void run() {
        while (true) {
            runOneIteration();
        }
    }

    void runOneIteration() {
        LocalDateTime timeBeforeReadingMessagesFromNetwork = LocalDateTime.now();

        while (existsMessageFromNetwork()) {
            String incomingEvent = readIncomingEvent();
            eventHandler.handle(incomingEvent);
        }

        waitRemainderOfTurn(timeBeforeReadingMessagesFromNetwork);
        eventProducer.produce();
    }

    private boolean existsMessageFromNetwork() {
        try {
            return bufferedReader.ready();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String readIncomingEvent() {
        try {
            return bufferedReader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void waitRemainderOfTurn(LocalDateTime beforeReadingMessagesFromNetwork) {
        Duration duration = Duration.between(beforeReadingMessagesFromNetwork, LocalDateTime.now());
        long millisToWait = 1000 - duration.toMillis();
        sleeper.sleep(TimeUnit.MILLISECONDS, millisToWait);
    }
}
