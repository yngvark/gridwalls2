package com.yngvark.gridwalls.microservices.zombie.game.os_process;

import com.yngvark.gridwalls.microservices.zombie.game.GameCleanup;
import com.yngvark.gridwalls.microservices.zombie.game.GameRunner;
import com.yngvark.gridwalls.microservices.zombie.game.ServerMessagesConsumer;

public class ProcessRunner {
    private final ShutdownHook shutdownHook;
    private final ServerMessagesConsumer serverMessagesConsumer;
    private final GameRunner gameRunner;
    private final GameCleanup gameCleanup;

    public ProcessRunner(ShutdownHook shutdownHook, ServerMessagesConsumer serverMessagesConsumer,
            GameRunner gameRunner, GameCleanup gameCleanup) {
        this.shutdownHook = shutdownHook;
        this.serverMessagesConsumer = serverMessagesConsumer;
        this.gameRunner = gameRunner;
        this.gameCleanup = gameCleanup;
    }

    public void run() {
        initShutdownhook();

        try {
            serverMessagesConsumer.startConsumingEvents();
            gameRunner.run();
            gameCleanup.cleanupAfterGameComplete();
        } catch (Throwable t) {
            System.out.println("Got exception. Shutting down.");
            t.printStackTrace();
            shutdownHook.shutdown();
        }
    }

    private void initShutdownhook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("Shutdownhook called from outside process.");
                shutdownHook.shutdown();
            }
        });
    }
}
