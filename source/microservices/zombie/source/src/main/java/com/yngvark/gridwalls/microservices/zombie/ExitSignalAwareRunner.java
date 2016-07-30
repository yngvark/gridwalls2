package com.yngvark.gridwalls.microservices.zombie;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

class ExitSignalAwareRunner {
    public void run(GameRunner gameRunner) throws IOException, TimeoutException, InterruptedException {
        Object waitForOkToShutdownApplicationProcess = new Object();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                super.run();

                // We want to let the game finish before exiting the game process. As soon as we exit this method the application process is
                // terminated, no matter if the other thread running the game has terminated or not. So in order to stop the game gracefully, we'll
                // signal the game runner to exit the game, which will stop the game runner. Then we'll wait for the signal that the game runner has
                // completed exiting the game.

                System.out.println("Exiting gracefully...");
                gameRunner.initateStop();

                synchronized (waitForOkToShutdownApplicationProcess) {
                    try {
                        System.out.println("Waiting for game to complete...");
                        waitForOkToShutdownApplicationProcess.wait();
                        System.out.println("Waiting for game to complete... done.");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // Blocking call.
        gameRunner.runGame();

        System.out.println("Notifying gameCompleteLock...");
        synchronized (waitForOkToShutdownApplicationProcess) {
            waitForOkToShutdownApplicationProcess.notify();
        }
        System.out.println("Notifying gameCompleteLock... done.");

        System.out.println("Exiting gracefully... done.");
    }
}
