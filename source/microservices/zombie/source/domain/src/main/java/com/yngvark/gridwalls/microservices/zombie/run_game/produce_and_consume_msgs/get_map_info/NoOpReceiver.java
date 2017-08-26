package com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs.get_map_info;

import com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs.NetworkMsgListener;
import com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs.NetworkMsgListenerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class NoOpReceiver implements NetworkMsgListener {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void messageReceived(NetworkMsgListenerContext networkMsgListenerContext, String msg) {
        logger.info("Discarding message: {}", msg);
    }
}
