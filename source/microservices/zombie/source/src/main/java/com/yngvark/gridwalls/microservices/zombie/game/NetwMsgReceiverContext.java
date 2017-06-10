package com.yngvark.gridwalls.microservices.zombie.game;

import com.yngvark.gridwalls.microservices.zombie.app.NetworkMessageListener;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

class NetwMsgReceiverContext implements NetworkMessageListener {
    private final Logger logger = getLogger(getClass());

    private NetGameMsgListener currentListener;

    public NetwMsgReceiverContext(NetGameMsgListener currentListener) {
        this.currentListener = currentListener;
    }

    @Override
    public void messageReceived(String msg) {
        logger.info("<<< (> {}) {}", currentListener.getClass().getSimpleName(), msg);
        currentListener.messageReceived(this, msg);
    }

    public void setCurrentListener(NetGameMsgListener currentListener) {
        this.currentListener = currentListener;
    }
}
