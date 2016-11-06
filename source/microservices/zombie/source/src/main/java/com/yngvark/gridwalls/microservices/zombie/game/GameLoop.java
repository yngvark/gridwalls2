package com.yngvark.gridwalls.microservices.zombie.game;

import com.yngvark.gridwalls.microservices.zombie.game.utils.GameErrorHandler;
import com.yngvark.gridwalls.microservices.zombie.game.utils.SafeMessageFormatter;
import com.yngvark.gridwalls.microservices.zombie.game.utils.Sleeper;
import com.yngvark.gridwalls.netcom.gameconfig.GameConfig;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class GameLoop {
    private final ZombiesController zombiesController;
    private final GameErrorHandler gameErrorHandler;
    private final Sleeper sleeper;
    private final BlockingQueue blockingQueue;

    private boolean runLoop = true;

    public GameLoop(ZombiesController zombiesController, GameErrorHandler gameErrorHandler,
            Sleeper sleeper, BlockingQueue blockingQueue) {
        this.zombiesController = zombiesController;
        this.gameErrorHandler = gameErrorHandler;
        this.sleeper = sleeper;
        this.blockingQueue = blockingQueue;
    }

    public void run(GameConfig gameConfig) {
        int i = 0;
        while (i < 100 && runLoop) {
            i++;
            try {
                zombiesController.nextTurn();
            } catch (Throwable e) {
                gameErrorHandler.handle(e);
                System.out.println("Aborting due to errors. Details: " + gameErrorHandler.getErrors());
                break;
            }

            sleeper.sleep();
        }

        signalDone();
    }

    private void signalDone() {
        try {
            System.out.println("GameLoop: Signaling stop to continue.");
            blockingQueue.put("loop has exited");
        } catch (InterruptedException e) {
            System.out.println("WARN: Interrupted while putting signal to blocking queue. Details: "+ e.getMessage());
        }
    }

    public void stopLoopAndWaitUntilItCompletes() {
        runLoop = false;
        waitForDoneSignal();
    }

    private void waitForDoneSignal() {
        try {
            Object nullIfTimeout = blockingQueue.poll(10, TimeUnit.SECONDS);
            if (nullIfTimeout == null)
                System.out.println("WARN: Never received game-loop-done signal. Timed out.");
            else
                System.out.println("Received signal: " + nullIfTimeout);
        } catch (InterruptedException e) {
            System.out.println("WARN: Interrupted while waiting for game loop to stop. Details: " + e.getMessage());
        }
    }
}
