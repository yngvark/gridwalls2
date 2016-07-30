package com.yngvark.gridwalls.microservices.zombie;

import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

class GameRunner {
    private final RabbitMqConnector rabbitMqConnector;
    private final ZombieRunnableFactory zombieRunnableFactory;
    private final ZombieFactory zombieFactory;
    private final GameErrorHandler gameErrorHandler;

    private boolean continueRunning = true;

    public GameRunner(RabbitMqConnector rabbitMqConnector, ZombieRunnableFactory zombieRunnableFactory,
            ZombieFactory zombieFactory, GameErrorHandler gameErrorHandler) {
        this.rabbitMqConnector = rabbitMqConnector;
        this.zombieRunnableFactory = zombieRunnableFactory;
        this.zombieFactory = zombieFactory;
        this.gameErrorHandler = gameErrorHandler;
    }

    public void runGame() throws IOException, TimeoutException, InterruptedException {
        // Connect to server
        Connection connection = rabbitMqConnector.connect();

        // Connect to server and get following info. For now, just assume these.
        int mapHeight = 10;
        int mapWidth = 10;
        List<Zombie> zombies = zombieFactory.createZombies(mapHeight, mapWidth);

        List<Runnable> zombieRunnables = zombieRunnableFactory.createZombieRunnables(zombies, connection);

        run(zombieRunnables);

        rabbitMqConnector.disconnect();
    }

    private void run(List<Runnable> zombieRunnables) throws IOException, InterruptedException, TimeoutException {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        scheduleRunnables(zombieRunnables, executor);
        runGameloop();

        System.out.println("Stopping game...");
        executor.shutdown();
        executor.awaitTermination(3, TimeUnit.SECONDS);
        System.out.println("Stopping game... done.");
    }

    private void scheduleRunnables(List<Runnable> zombieRunnables, ScheduledExecutorService executor) {
        for (int i = 0; i < zombieRunnables.size(); i++) {
            executor.scheduleWithFixedDelay(zombieRunnables.get(i), i, zombieRunnables.size(), TimeUnit.SECONDS);
        }
    }

    private void runGameloop() throws InterruptedException {
        do {
            if (gameErrorHandler.receivedErrors()) {
                System.out.println("Error occurred, exiting game. Errors::");
                System.out.println(gameErrorHandler.getErrors());
                break;
            }

            Thread.sleep(1000);
        } while (continueRunning);
    }


    public void initateStop() {
        continueRunning = false;
    }
}
