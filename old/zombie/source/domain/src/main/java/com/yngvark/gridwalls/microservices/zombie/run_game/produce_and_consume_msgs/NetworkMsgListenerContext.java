package com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs;

import com.yngvark.gridwalls.microservices.zombie.run_game.NetworkMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkMsgListenerContext implements NetworkMessageListener {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private NetworkMsgListener currentListener;

    public NetworkMsgListenerContext(NetworkMsgListener currentListener) {
        this.currentListener = currentListener;
    }

    @Override
    public void messageReceived(String msg) {
        logger.info("<<< (> {}) {}", currentListener.getClass().getSimpleName(), msg);
        currentListener.messageReceived(this, msg);
    }

    public void setCurrentListener(NetworkMsgListener currentListener) {
        this.currentListener = currentListener;
    }
}
