package com.yngvark.gridwalls.microservices.zombie.run_game.get_map_info;

import com.yngvark.gridwalls.microservices.zombie.run_game.NetworkMsgListener;
import com.yngvark.gridwalls.microservices.zombie.run_game.NetworkMsgListenerContext;

class NoOpReceiver implements NetworkMsgListener {
    @Override
    public void messageReceived(NetworkMsgListenerContext networkMsgListenerContext, String msg) {

    }
}
