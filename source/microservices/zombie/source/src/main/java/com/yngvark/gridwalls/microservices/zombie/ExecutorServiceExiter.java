package com.yngvark.gridwalls.microservices.zombie;

import com.google.inject.Inject;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceExiter {
    private final StackTracePrinter stackTracePrinter;

    @Inject
    public ExecutorServiceExiter(StackTracePrinter stackTracePrinter) {
        this.stackTracePrinter = stackTracePrinter;
    }

    public void exitGracefully(ExecutorService executorService) {
        System.out.println("Exiting gracefully.");

        executorService.shutdown();

        boolean terminatedBeforeTimeout;
        try {
            terminatedBeforeTimeout = executorService.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            stackTracePrinter.print("Interrupted while awaiting termination. Cannot exit gracefully. Exiting.", e);
            return;
        }

        if (!terminatedBeforeTimeout) {
            System.out.println("Exiting gracefully timed out. Calling shutdownNow.");
            List<Runnable> unstartedRunnables = executorService.shutdownNow();
            System.out.println("Number of tasks not able to be shut down: " + unstartedRunnables.size());
            return;
        }

        System.out.println("Exiting gracefully was successful. " + executorService.isTerminated());
    }
}
