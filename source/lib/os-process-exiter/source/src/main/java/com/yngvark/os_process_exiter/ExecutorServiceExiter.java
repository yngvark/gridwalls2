package com.yngvark.os_process_exiter;

import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;

public class ExecutorServiceExiter {
    private static final Logger logger = getLogger(ExecutorServiceExiter.class);

    public static void exitGracefully(ExecutorService executorService) {
        logger.info("Exiting gracefully.");

        if (executorService.isShutdown()) {
            logger.info("Executor service already shut down.");
            return;
        }

        executorService.shutdown();

        boolean terminatedBeforeTimeout;
        try {
            terminatedBeforeTimeout = executorService.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.info("Interrupted while awaiting termination. Cannot exit gracefully. Exiting.");
            e.printStackTrace();
            return;
        }

        if (!terminatedBeforeTimeout) {
            logger.info("Exiting gracefully timed out. Forcing shutdown.");
            List<Runnable> unstartedRunnables = executorService.shutdownNow();
            logger.info("Number of tasks not able to be shut down: " + unstartedRunnables.size());
            return;
        }

        logger.info("Exiting gracefully was successful.");
    }
}
