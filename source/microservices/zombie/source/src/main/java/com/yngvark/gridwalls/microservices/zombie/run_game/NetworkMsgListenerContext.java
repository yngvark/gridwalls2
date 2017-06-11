package com.yngvark.gridwalls.microservices.zombie.run_game;

import com.yngvark.gridwalls.microservices.zombie.run_app.NetworkMessageListener;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class NetworkMsgListenerContext implements NetworkMessageListener {
    private final Logger logger = getLogger(getClass());

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
