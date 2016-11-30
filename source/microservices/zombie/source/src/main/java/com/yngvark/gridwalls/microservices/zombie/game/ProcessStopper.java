package com.yngvark.gridwalls.microservices.zombie.game;

import com.yngvark.gridwalls.microservices.zombie.game.os_process.ExecutorServiceExiter;
import com.yngvark.gridwalls.netcom.Netcom;

public class ProcessStopper {
    private final GameLoopRunner gameRunnerLoop;
    private final Netcom netcom;
    private final ExecutorServiceExiter executorServiceExiter;

    public ProcessStopper(GameLoopRunner gameRunnerLoop, Netcom netcom,
            ExecutorServiceExiter executorServiceExiter) {
        this.gameRunnerLoop = gameRunnerLoop;
        this.netcom = netcom;
        this.executorServiceExiter = executorServiceExiter;
    }

    /**
     * Stops the game. Runs only once, even when called from multiple threads.
     */
    public synchronized void stop() {
        executorServiceExiter.exitGracefully();
        netcom.disconnectAndDisableReconnect();

    }

}
