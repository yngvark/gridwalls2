package com.yngvark.gridwalls.microservices.netcom_forwarder.exit_os_process;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceExiter {

    public static void exitGracefully(ExecutorService executorService) {
        System.out.println("Exiting gracefully.");

        if (executorService.isShutdown()) {
            System.out.println("Executor service already shut down.");
            return;
        }

        executorService.shutdown();

        boolean terminatedBeforeTimeout;
        try {
            terminatedBeforeTimeout = executorService.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("Interrupted while awaiting termination. Cannot exit gracefully. Exiting.");
            e.printStackTrace();
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
