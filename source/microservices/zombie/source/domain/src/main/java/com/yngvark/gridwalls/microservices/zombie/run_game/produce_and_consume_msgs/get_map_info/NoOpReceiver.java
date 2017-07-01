package com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs.get_map_info;

import com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs.NetworkMsgListener;
import com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs.NetworkMsgListenerContext;

class NoOpReceiver implements NetworkMsgListener {
    @Override
    public void messageReceived(NetworkMsgListenerContext networkMsgListenerContext, String msg) {

    }
}
