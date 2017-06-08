package com.yngvark.gridwalls.microservices.zombie.game;

class NoOpReceiver implements NetGameMsgListener {
    @Override
    public void messageReceived(NetwMsgReceiverContext netwMsgReceiverContext, String msg) {

    }
}
