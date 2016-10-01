package com.yngvark.gridwalls.microservices.zombie.infrastructure;

import com.yngvark.gridwalls.microservices.zombie.gamelogic.GameRunner;
import com.yngvark.gridwalls.netcom.Netcom;

import java.util.concurrent.ExecutorService;

public class ProcessRunner {
    private final ExecutorService executorService;
    private final ExecutorServiceExiter executorServiceExiter;
    private final GameRunner gameRunner;
    private final Netcom netcom;

    public ProcessRunner(ExecutorService executorService,
            ExecutorServiceExiter executorServiceExiter,
            GameRunner gameRunner,
            Netcom netcom) {
        this.executorService = executorService;
        this.executorServiceExiter = executorServiceExiter;
        this.gameRunner = gameRunner;
        this.netcom = netcom;
    }

    public void run() {
        initShutdownhook();
        gameRunner.run();
        netcom.disconnectIfConnected();
        exit();
    }

    private void initShutdownhook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("Running shutdownhook...");
                exit();
                System.out.println("Running shutdownhook... Done.");
            }
        });
    }

    private void exit() {
        System.out.println("Exiting normally");
        executorServiceExiter.exitGracefully(executorService);
        netcom.disconnectIfConnected();
    }

}
