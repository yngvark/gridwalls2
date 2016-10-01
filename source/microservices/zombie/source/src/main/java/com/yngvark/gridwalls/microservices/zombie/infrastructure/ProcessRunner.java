package com.yngvark.gridwalls.microservices.zombie.infrastructure;

import com.yngvark.gridwalls.microservices.zombie.gamelogic.GameRunner;

import java.util.concurrent.ExecutorService;

public class ProcessRunner {
    private final ExecutorService executorService;
    private final ExecutorServiceExiter executorServiceExiter;
    private final GameRunner gameRunner;
    private final BrokerConnecter brokerConnecter;

    public ProcessRunner(ExecutorService executorService,
            ExecutorServiceExiter executorServiceExiter, GameRunner gameRunner,
            BrokerConnecter brokerConnecter) {
        this.executorService = executorService;
        this.executorServiceExiter = executorServiceExiter;
        this.gameRunner = gameRunner;
        this.brokerConnecter = brokerConnecter;
    }

    public void run() {
        initShutdownhook();
        gameRunner.run();
        disconnectIfConnected();
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

    private void disconnectIfConnected() {
    }

    private void exit() {
        System.out.println("Exit run in gamerunner.");
        brokerConnecter.disconnectIfConnected();
        executorServiceExiter.exitGracefully(executorService);
        System.out.println("Exit run in gamerunner... Done.");
    }

}
