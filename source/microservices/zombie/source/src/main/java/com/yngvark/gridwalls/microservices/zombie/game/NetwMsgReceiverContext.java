package com.yngvark.gridwalls.microservices.zombie.game;

import com.yngvark.gridwalls.microservices.zombie.app.NetworkMessageListener;

class NetwMsgReceiverContext implements NetworkMessageListener {
    private NetGameMsgListener currentListener;

    public NetwMsgReceiverContext(NetGameMsgListener currentListener) {
        this.currentListener = currentListener;
    }

    @Override
    public void messageReceived(String msg) {
        currentListener.messageReceived(this, msg);
    }

    public void setCurrentListener(NetGameMsgListener currentListener) {
        this.currentListener = currentListener;
    }
}
