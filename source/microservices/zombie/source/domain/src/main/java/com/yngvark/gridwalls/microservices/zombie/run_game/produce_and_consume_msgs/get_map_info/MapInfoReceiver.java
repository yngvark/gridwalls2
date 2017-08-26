package com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs.get_map_info;

import com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs.NetworkMsgListener;
import com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs.NetworkMsgListenerContext;
import com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs.Producer;
import com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs.ProducerContext;
import com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs.move.ZombieMoverFactory;
import com.yngvark.gridwalls.microservices.zombie.run_game.serialize_msgs.Serializer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MapInfoReceiver implements Producer, NetworkMsgListener {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Serializer serializer;
    private final ZombieMoverFactory zombieMoverFactory;

    private BlockingQueue<String> messages = new LinkedBlockingQueue<>();
    private ProducerContext producerContext;

    public MapInfoReceiver(Serializer serializer,
            ZombieMoverFactory zombieMoverFactory) {
        this.serializer = serializer;
        this.zombieMoverFactory = zombieMoverFactory;
        enqueueMessage("/subscribeTo Zombie_MapInfo");

        String mapInfoRequest = serializer.serialize(
                new MapInfoRequest().replyToTopic("Zombie_MapInfo"));
        enqueueMessage("/publishTo MapInfoRequests " + mapInfoRequest);
    }

    private void enqueueMessage(String msg) {
        logger.info("Enquing message: {}", msg);
        try {
            messages.put(msg);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String nextMsg(ProducerContext producerContext) {
        logger.debug("Producing next.");
        try {
            this.producerContext = producerContext;
            logger.debug("Waiting for message in queue...");
            String msg = messages.take();
            logger.debug("Waiting for message in queue... Done: {}", msg);
            return msg;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void messageReceived(NetworkMsgListenerContext networkMsgListenerContext, String msg) {
        logger.info("Received: {}", msg);
        String[] parts = StringUtils.split(msg, " ", 2);

        MapInfo mapInfo = serializer.deserialize(parts[1], MapInfo.class);

        logger.info("Deserialize done.");
        networkMsgListenerContext.setCurrentListener(new NoOpReceiver());

        producerContext.setCurrentProducer(zombieMoverFactory.create(mapInfo));
        enqueueMessage(producerContext.nextMsg());
    }

}
