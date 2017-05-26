package com.yngvark.gridwalls.microservices.zombie.game.os_process;

import com.yngvark.gridwalls.microservices.zombie.game.utils.StackTracePrinter;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceExiter {
    private final ExecutorService executorService;
    private final StackTracePrinter stackTracePrinter;

    public ExecutorServiceExiter(ExecutorService executorService,
            StackTracePrinter stackTracePrinter) {
        this.executorService = executorService;
        this.stackTracePrinter = stackTracePrinter;
    }

    public synchronized void exitGracefully() {
        System.out.println("Shutdownhook: Exiting gracefully.");

        if (executorService.isShutdown()) {
            System.out.println("Shutdownhook not necessary, because " + ExecutorService.class.getSimpleName() + " is already shut down.");
            return;
        }

        executorService.shutdown();

        boolean terminatedBeforeTimeout;
        try {
            terminatedBeforeTimeout = executorService.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            stackTracePrinter.print("Interrupted while awaiting termination. Cannot exit gracefully. Exiting.", e);
            return;
        }

        if (!terminatedBeforeTimeout) {
            System.out.println("Exiting gracefully timed out. Forcing shutdown.");
            List<Runnable> unstartedRunnables = executorService.shutdownNow();
            System.out.println("Number of tasks not able to be shut down: " + unstartedRunnables.size());
            return;
        }

        System.out.println("Exiting gracefully was successful.");
    }
}
