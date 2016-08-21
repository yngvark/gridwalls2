package com.yngvark.gridwalls.microservices.zombie;

import com.google.inject.Inject;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class GameRunner4 {
    private final SystemInReader systemInReader;
    private final ExecutorService executorService;
    private final ExecutorServiceExiter executorServiceExiter;

    private final StackTracePrinter stackTracePrinter;

    private Future systemInReaderFuture;

    @Inject
    public GameRunner4(SystemInReader systemInReader, ExecutorService executorService,
            ExecutorServiceExiter executorServiceExiter, StackTracePrinter stackTracePrinter) {
        this.systemInReader = systemInReader;
        this.executorService = executorService;
        this.executorServiceExiter = executorServiceExiter;
        this.stackTracePrinter = stackTracePrinter;
    }

    public void run() {
        initShutdownhook();
        readSystemInput();
        exit();
    }

    private void initShutdownhook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                super.run();
                System.out.println("Running shutdownhook.");
                executorServiceExiter.exitGracefully(executorService);
            }
        });
    }

    private void readSystemInput() {
        systemInReaderFuture = executorService.submit(() -> systemInReader.run());

        try {
            systemInReaderFuture.get();
        } catch (InterruptedException e) {
            stackTracePrinter.print("Reading stdin was interrupted.", e);
        } catch (ExecutionException e) {
            stackTracePrinter.print("Error while reading stdin.", e);
        }
    }

    private void exit() {
        System.out.println("Exit run in gamerunner. 1");
        executorServiceExiter.exitGracefully(executorService);
        System.out.println("Exit run in gamerunner... Done.");

    }

}
