package com.yngvark.gridwalls.microservices.zombie.game;

import com.yngvark.gridwalls.microservices.zombie.game.serialize_events.Serializer;
import org.slf4j.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.slf4j.LoggerFactory.getLogger;

class MapInfoReceiver implements Producer, NetGameMsgListener {
    private final Logger logger = getLogger(getClass());

    private final Serializer serializer;
    private final ZombieMoverFactory zombieMoverFactory;

    private boolean subscribedToMapInfo = false;
    private MapInfo mapInfo;
    private BlockingQueue blockingQueue = new LinkedBlockingQueue();
    private boolean mapInfoReceived = false;

    public MapInfoReceiver(Serializer serializer,
            ZombieMoverFactory zombieMoverFactory) {
        this.serializer = serializer;
        this.zombieMoverFactory = zombieMoverFactory;
    }

    public String nextMsg(ProducerContext producerContext) {
        if (!subscribedToMapInfo) {
            subscribedToMapInfo = true;
            return "/subscribeTo MapInfo";
        }

        if (!mapInfoReceived) {
            try {
                logger.info("Waiting for map info...");
                blockingQueue.take();
                logger.info("Waiting for map info... done: " + mapInfo);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        producerContext.setCurrentProducer(zombieMoverFactory.create(mapInfo));
        return producerContext.nextMsg();
    }

    public void messageReceived(NetwMsgReceiverContext netwMsgReceiverContext, String msg) {
        logger.debug("<<< {}", msg);
        mapInfo = serializer.deserialize(msg, MapInfo.class);
        mapInfoReceived = true;
        try {
            blockingQueue.put("We have received mapInfo, proceed.");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        netwMsgReceiverContext.setCurrentListener(new NoOpReceiver());
    }
}
