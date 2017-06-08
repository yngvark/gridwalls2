package com.yngvark.gridwalls.microservices.zombie.game;

interface NetGameMsgListener {
    void messageReceived(NetwMsgReceiverContext netwMsgReceiverContext, String msg);
}
