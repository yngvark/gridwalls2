package com.yngvark.gridwalls.microservices.zombie.game;

import com.yngvark.gridwalls.microservices.zombie.game.utils.GameErrorHandler;
import com.yngvark.gridwalls.microservices.zombie.game.utils.Sleeper;
import com.yngvark.gridwalls.netcom.gameconfig.GameConfig;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class GameRunnerLoop {
    private final GameLoopFactory gameLoopFactory;
    private final GameErrorHandler gameErrorHandler;
    private final Sleeper sleeper;
    private final BlockingQueue blockingQueue;

    private boolean runLoop = true;
    private boolean runLoopStarted = false;

    public GameRunnerLoop(GameLoopFactory gameLoopFactory, GameErrorHandler gameErrorHandler, Sleeper sleeper, BlockingQueue blockingQueue) {
        this.gameLoopFactory = gameLoopFactory;
        this.gameErrorHandler = gameErrorHandler;
        this.sleeper = sleeper;
        this.blockingQueue = blockingQueue;
    }

    public void run(GameConfig gameConfig) {
        if (!runLoop) {
            System.out.println("Not running game loop.");
            return;
        }

        System.out.println("Starting game loop.");
        runLoopStarted = true;

        GameLoop gameLoop = gameLoopFactory.create(gameConfig);
        int i = 0;
        while (i < 100 && runLoop) {
            i++;
            try {
                gameLoop.nextTurn();
            } catch (Throwable e) {
                gameErrorHandler.handle(e);
                System.out.println("Aborting due to errors. Details: " + gameErrorHandler.getErrors());
                break;
            }

            sleeper.sleep();
        }

        signalDone();
        System.out.println("GameLoop stopped.");
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
        System.out.println("Stopping game loop");
        runLoop = false;

        if (runLoopStarted)
            waitForDoneSignal();
    }

    private void waitForDoneSignal() {
        try {
            System.out.println("Waiting for done signal");
            Object nullIfTimeout = blockingQueue.poll(2, TimeUnit.SECONDS);
            System.out.println("Waiting for done signal - done");

            if (nullIfTimeout == null)
                System.out.println("WARN: Never received game-loop-done signal. Timed out.");
            else
                System.out.println("Received signal: " + nullIfTimeout);
        } catch (InterruptedException e) {
            System.out.println("WARN: Interrupted while waiting for game loop to stop. Details: " + e.getMessage());
        }
    }
}
