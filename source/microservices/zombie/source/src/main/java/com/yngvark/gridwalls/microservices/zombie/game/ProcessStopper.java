package com.yngvark.gridwalls.microservices.zombie.game;

import com.yngvark.gridwalls.microservices.zombie.game.os_process.ExecutorServiceExiter;
import com.yngvark.gridwalls.netcom.Netcom;

public class ProcessStopper {
    private final GameRunnerLoop gameRunnerLoop;
    private final Netcom netcom;
    private final ExecutorServiceExiter executorServiceExiter;

    private boolean stopped = false;

    public ProcessStopper(GameRunnerLoop gameRunnerLoop, Netcom netcom,
            ExecutorServiceExiter executorServiceExiter) {
        this.gameRunnerLoop = gameRunnerLoop;
        this.netcom = netcom;
        this.executorServiceExiter = executorServiceExiter;
    }

    /**
     * Stops the game. Runs only once, even when called from multiple threads.
     */
    public synchronized void stop() {
        System.out.println("Stopping process. Already stopped: " + stopped);

        if (stopped)
            return;
        stopped = true;

        gameRunnerLoop.stopLoopAndWaitUntilItCompletes();
        executorServiceExiter.exitGracefully();
        netcom.disconnectAndDisableReconnect();

        System.out.println("Stopping process done.");
    }

}
