package com.yngvark.gridwalls.microservices.zombie.game.os_process;

import com.yngvark.gridwalls.microservices.zombie.game.GameRunner;
import com.yngvark.gridwalls.microservices.zombie.game.ProcessStopper;

import java.util.concurrent.ExecutorService;

public class ProcessRunner {
    private final GameRunner gameRunner;
    private final ProcessStopper processStopper;

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
                processStopper.stop();
            }
        });
    }
}
