package com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs;

public interface NetworkMsgListener {
    void messageReceived(NetworkMsgListenerContext networkMsgListenerContext, String msg);
}
