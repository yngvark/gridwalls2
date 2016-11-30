package com.yngvark.gridwalls.microservices.zombie.game.os_process;

import com.yngvark.gridwalls.microservices.zombie.game.GameRunner;
import com.yngvark.gridwalls.microservices.zombie.game.ProcessStopper;

public class ProcessRunner {
    private final GameRunner gameRunner;
    private final ProcessStopper processStopper;

    private boolean stopped = false;

    public ProcessRunner(GameRunner gameRunner, ProcessStopper processStopper) {
        this.gameRunner = gameRunner;
        this.processStopper = processStopper;
    }

    public void run() {
        initShutdownhook();
        gameRunner.run();
        processStopper.stop();
    }

    private void initShutdownhook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("Stopping process. Already stopped: " + stopped);
                if (stopped)
                    return;
                stopped = true;

                gameRunner.stopAndWaitUntilStopped();

                processStopper.stop();
                System.out.println("Stopping process done.");
            }
        });
    }
}
