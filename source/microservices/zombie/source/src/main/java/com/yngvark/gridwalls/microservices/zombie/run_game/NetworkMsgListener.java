package com.yngvark.gridwalls.microservices.zombie.run_game;

public interface NetworkMsgListener {
    void messageReceived(NetworkMsgListenerContext networkMsgListenerContext, String msg);
}
