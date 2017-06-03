package com.yngvark.gridwalls.microservices.netcom_forwarder.exit_os_process;

import com.yngvark.gridwalls.microservices.netcom_forwarder.app.App;
import com.yngvark.os_process_exiter.ExecutorServiceExiter;
import org.slf4j.Logger;

import java.util.concurrent.ExecutorService;

import static org.slf4j.LoggerFactory.getLogger;

public class Shutdownhook {
    private final Logger logger = getLogger(getClass());
    private final App app;

    public Shutdownhook(App app) {
        this.app = app;
    }

    public void run(ExecutorService executorService) {
        logger.info("Running shutdownhook...");
        app.stop();
        ExecutorServiceExiter.exitGracefully(executorService);
        logger.info("Running shutdownhook... done.");
    }
}
