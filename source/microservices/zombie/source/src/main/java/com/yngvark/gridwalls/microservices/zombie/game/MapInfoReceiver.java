package com.yngvark.gridwalls.microservices.zombie.game;

import com.yngvark.gridwalls.microservices.zombie.game.serialize_events.Serializer;
import org.slf4j.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.slf4j.LoggerFactory.getLogger;

class MapInfoReceiver implements GameLogic, NetGameMsgListener {
    private final Logger logger = getLogger(getClass());

    private final Serializer serializer;
    private final ZombieMoverFactory zombieMoverFactory;

    private MapInfo mapInfo;
    private BlockingQueue blockingQueue = new LinkedBlockingQueue();
    private boolean mapInfoReceived = false;

    public MapInfoReceiver(Serializer serializer,
            ZombieMoverFactory zombieMoverFactory) {
        this.serializer = serializer;
        this.zombieMoverFactory = zombieMoverFactory;
    }

    public String nextMsg(GameLogicContext gameLogicContext) {
        if (!mapInfoReceived) {
            try {
                blockingQueue.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        gameLogicContext.setCurrentGameLogic(zombieMoverFactory.create(mapInfo));
        return gameLogicContext.nextMsg();
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
