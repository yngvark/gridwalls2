package com.yngvark.gridwalls.microservices.zombie;

import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

class GameRunner implements ICanExitOnSignal {
    private final RabbitMqConnector rabbitMqConnector;
    private final ZombieRunnableFactory zombieRunnableFactory;
    private final ZombieFactory zombieFactory;
    private final GameErrorHandler gameErrorHandler;
    private GameConfigFetcher gameConfigFetcher = new GameConfigFetcher();

    private boolean exitSignalReceived = false;

    public GameRunner(RabbitMqConnector rabbitMqConnector, ZombieRunnableFactory zombieRunnableFactory,
            ZombieFactory zombieFactory, GameErrorHandler gameErrorHandler) {
        this.rabbitMqConnector = rabbitMqConnector;
        this.zombieRunnableFactory = zombieRunnableFactory;
        this.zombieFactory = zombieFactory;
        this.gameErrorHandler = gameErrorHandler;
    }

    public void runGame() {
        try {
            tryToRun();
        } catch (Exception e) {
            System.out.println("Error occured. Exiting game. Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void tryToRun() throws IOException, TimeoutException, InterruptedException {
        do {
            // publish GetGameConfig, (reposnse) -> gameCOnfigReceived = true



            Thread.sleep(1000);
        } while (!exitSignalReceived);
    }

    private void tryToRun2() throws IOException, TimeoutException, InterruptedException {
        // Connect to server
        Connection connection = rabbitMqConnector.connect();

        // TODO Må være mulig å avbryte RPC-kallet også.
        GameConfig gameConfig = gameConfigFetcher.getGameConfigFromServer(connection);

        if (!exitSignalReceived) {
            runGame(connection, gameConfig);
        }

        rabbitMqConnector.disconnect(); // Also disconnects channels.
        System.out.println("Game exiting.");
    }

    private void runGame(Connection connection, GameConfig gameConfig) throws IOException, InterruptedException, TimeoutException {
        int mapHeight = gameConfig.getMapHeight();
        int mapWidth = gameConfig.getMapWidth();
        List<Zombie> zombies = zombieFactory.createZombies(mapHeight, mapWidth);

        List<Runnable> zombieRunnables = zombieRunnableFactory.createZombieRunnables(zombies, connection);

        runGame(zombieRunnables);
    }

    private void runGame(List<Runnable> zombieRunnables) throws IOException, InterruptedException, TimeoutException {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        scheduleRunnables(zombieRunnables, executor);
        runGameloop();

        executor.shutdown();
        executor.awaitTermination(3, TimeUnit.SECONDS);
    }

    private void scheduleRunnables(List<Runnable> zombieRunnables, ScheduledExecutorService executor) {
        for (int i = 0; i < zombieRunnables.size(); i++) {
            executor.scheduleWithFixedDelay(zombieRunnables.get(i), i, zombieRunnables.size(), TimeUnit.SECONDS);
        }
    }

    private void runGameloop() throws InterruptedException {
        do {
            if (gameErrorHandler.receivedErrors()) {
                System.out.println("Error occurred, exiting game. Errors:");
                System.out.println(gameErrorHandler.getErrors());
                break;
            }

            Thread.sleep(1000);
        } while (!exitSignalReceived);
        System.out.println("Game loop exited.");
    }


    public void exitSignalReceived() throws Exception {
        exitSignalReceived = true;
        gameConfigFetcher.exitSignalReceived();
    }
}
