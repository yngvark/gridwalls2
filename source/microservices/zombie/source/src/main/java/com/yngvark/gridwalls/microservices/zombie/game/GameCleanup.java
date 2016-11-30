package com.yngvark.gridwalls.microservices.zombie.game;

import com.yngvark.gridwalls.microservices.zombie.game.os_process.ExecutorServiceExiter;
import com.yngvark.gridwalls.netcom.Netcom;

/**
 * Used to clean up after an already stopped game.
 */
public class GameCleanup {
    private final Netcom netcom;
    private final ExecutorServiceExiter executorServiceExiter;

    private boolean stopped = false;

    public GameCleanup(Netcom netcom, ExecutorServiceExiter executorServiceExiter) {
        this.netcom = netcom;
        this.executorServiceExiter = executorServiceExiter;
    }

    public synchronized void cleanupAfterGameComplete() {
        System.out.println("Stopping processess. Already stopped: " + stopped);
        if (stopped)
            return;
        stopped = true;

        executorServiceExiter.exitGracefully();
        netcom.disconnectAndDisableReconnect();
    }

}
