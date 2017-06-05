package com.yngvark.gridwalls.microservices.zombie.app;

import com.yngvark.communicate_through_named_pipes.input.MessageListener;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class NetworkMessageListener implements MessageListener {
    private final Logger logger = getLogger(getClass());

    @Override
    public void messageReceived(String msg) {
        logger.info("<<< From network: " + msg);
    }
}
