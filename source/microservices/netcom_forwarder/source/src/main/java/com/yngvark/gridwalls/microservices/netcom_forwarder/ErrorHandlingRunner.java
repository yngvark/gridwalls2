package com.yngvark.gridwalls.microservices.netcom_forwarder;

import com.yngvark.gridwalls.microservices.netcom_forwarder.app.App;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

class ErrorHandlingRunner {
    private final Logger logger = getLogger(getClass());

    public void run(App app) {
        try {
            app.run();
            app.stop();
        } catch (Throwable throwable) {
            logger.info("Error occured. Exiting");
            throwable.printStackTrace();
        }
    }
}
