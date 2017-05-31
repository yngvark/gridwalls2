package com.yngvark.gridwalls.microservices.zombie.game.os_process;

import com.yngvark.gridwalls.microservices.zombie.game.GameRunner;
import com.yngvark.gridwalls.microservices.zombie.game.GameCleanup;

/**
 * Used to stop the game and process in the middle of a game, i.e. a sudden exit.
 */
public class ShutdownHook {
    private final GameRunner gameRunner;
    private final GameCleanup gameCleanup;

    private boolean started = false;

    public ShutdownHook(GameRunner gameRunner, GameCleanup gameCleanup) {
        this.gameRunner = gameRunner;
        this.gameCleanup = gameCleanup;
    }

    public synchronized void shutdown() {
        System.out.println("Shutdownhook started. Has already started: " + started);
        if (started)
            return;
        started = true;

        gameRunner.stopAndWaitUntilStopped();

        gameCleanup.cleanupAfterGameComplete();
        System.out.println("Shutdownhook done.");
    }
}
