package com.yngvark.gridwalls.microservices.zombie.run_app;

import com.yngvark.communicate_through_named_pipes.input.MessageListener;
import com.yngvark.gridwalls.microservices.zombie.run_game.NetworkMessageListener;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class NetworkMessageReceiver implements MessageListener {
    private final Logger logger = getLogger(getClass());
    private final NetworkMessageListener networkMessageListener;

    public NetworkMessageReceiver(
            NetworkMessageListener networkMessageListener) {
        this.networkMessageListener = networkMessageListener;
    }

    @Override
    public void messageReceived(String msg) {
        logger.info("<<< From network: " + msg);
        networkMessageListener.messageReceived(msg);
    }
}
