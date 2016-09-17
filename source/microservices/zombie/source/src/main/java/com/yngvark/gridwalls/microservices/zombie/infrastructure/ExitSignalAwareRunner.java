package com.yngvark.gridwalls.microservices.zombie.infrastructure;

import org.apache.commons.lang3.exception.ExceptionUtils;

public class ExitSignalAwareRunner {
    public void run(ICanRunAndAbort iCanRunAndAbort) {
        Object waitForOkToShutdownApplicationProcess = new Object();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                super.run();

                // We want to let the game finish before exiting the game process. As soon as we exit this method the application process is
                // terminated, no matter if the other thread running the game has terminated or not. So in order to stop the game gracefully, we'll
                // signal the game runner to exit the game, which will stop the game runner. Then we'll wait for the signal that the game runner has
                // completed exiting the game.

                System.out.println("Exiting gracefully: Signalling runnable to exit.");
                try {
                    iCanRunAndAbort.startAborting();
                } catch (Throwable e) {
                    System.out.println("Exiting gracefully: Received exception while notifying runnable about exit. Details: " + ExceptionUtils.getStackTrace(e));
                }

                synchronized (waitForOkToShutdownApplicationProcess) {
                    try {
                        System.out.println("Exiting gracefully: Waiting for runnable to complete...");
                        waitForOkToShutdownApplicationProcess.wait(4000l);
                        System.out.println("Exiting gracefully: Waiting for runnable to complete... done.");
                    } catch (InterruptedException e) {
                        System.out.println("Exception while waiting for runnable to complete. Details: " + ExceptionUtils.getStackTrace(e));
                    }
                }
            }
        });

        // Blocking call.
        System.out.println("Running runnable instance (" + ICanRunAndAbort.class.getSimpleName() + ")");
        try {
            iCanRunAndAbort.run();
        } catch (Throwable e) {
            System.out.println("Runnable threw an exception: " + ExceptionUtils.getStackTrace(e));
            e.printStackTrace();
        }

        System.out.println("Runnable completed running.");
        System.out.println("Notifying shutdown hook that it can exit the process...");
        synchronized (waitForOkToShutdownApplicationProcess) {
            waitForOkToShutdownApplicationProcess.notify();
        }
        System.out.println("Notifying shutdown hook that it can exit the process... done");
        System.out.println("Exiting gracefully.");
    }
}
