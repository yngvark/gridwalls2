package com.yngvark.gridwalls.microservices.netcom_forwarder.app;

import com.yngvark.communicate_through_named_pipes.input.MessageListener;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

class MicroserviceMsgListener implements MessageListener {
    private final Logger logger = getLogger(getClass());

    public void messageReceived(String msg) {
        this.logger.info("<<< From microservice: " + msg);
    }
}
