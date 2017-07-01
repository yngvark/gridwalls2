package com.yngvark.gridwalls.microservices.zombie;

import com.yngvark.gridwalls.microservices.zombie.run_app.App;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

class ErrorHandlingRunner {
    private final Logger logger = getLogger(getClass());

    public void run(App app) {
        try {
            app.run();
        } catch (Throwable throwable) {
            logger.info("Error occured. Exiting");
            throwable.printStackTrace();
        }
    }
}
