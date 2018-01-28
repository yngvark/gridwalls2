package com.yngvark.gridwalls.microservices.zombie.exit_os_process;

import com.yngvark.gridwalls.microservices.zombie.run_app.App;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class Shutdownhook {
    private final Logger logger = getLogger(getClass());
    private final App app;

    public Shutdownhook(App app) {
        this.app = app;
    }

    public void run() {
        logger.info("Running shutdownhook");
        app.stop();
    }
}
