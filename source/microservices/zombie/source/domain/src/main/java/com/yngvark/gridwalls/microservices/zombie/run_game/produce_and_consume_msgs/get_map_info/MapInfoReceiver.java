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
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class is used by TWO threads, produing messages and consuming messages.
 */
public class MapInfoReceiver implements Producer, NetworkMsgListener {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Serializer serializer;
    private final ZombieMoverFactory zombieMoverFactory;
    private final ScheduledExecutorService scheduledExecutorService;

    private BlockingQueue<String> messages = new LinkedBlockingQueue<>();
    private ProducerContext producerContext;
    private Future futureMapInfoRequest;

    public MapInfoReceiver(Serializer serializer,
            ZombieMoverFactory zombieMoverFactory,
            ScheduledExecutorService scheduledExecutorService) {
        this.serializer = serializer;
        this.zombieMoverFactory = zombieMoverFactory;
        this.scheduledExecutorService = scheduledExecutorService;
        subscribeToMapInfo();
        requestMapInfo();
        requestMapInfoAgainAndAgainIfNotReceived();
    }

    private void subscribeToMapInfo() {
        enqueueMessage("/subscribeTo Zombie_MapInfo");
    }

    private void enqueueMessage(String msg) {
        logger.info("Enquing message: {}", msg);
        try {
            messages.put(msg);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void requestMapInfo() {
        String mapInfoRequest = serializer.serialize(
                new MapInfoRequest().replyToTopic("Zombie_MapInfo"));
        enqueueMessage("/publishTo MapInfoRequests " + mapInfoRequest);
    }

    private void requestMapInfoAgainAndAgainIfNotReceived() {
        futureMapInfoRequest = scheduledExecutorService.scheduleWithFixedDelay(
                this::requestMapInfo,
                2, 2, TimeUnit.SECONDS);
    }

    public String nextMsg(ProducerContext producerContext) {
        logger.debug("Producing next.");
        try {
            this.producerContext = producerContext;
            String msg = waitForNextEnqueuedMessage();
            return msg;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String waitForNextEnqueuedMessage() throws InterruptedException {
        logger.debug("Waiting for message in queue...");
        String msg = messages.take();
        logger.debug("Waiting for message in queue... Done!");
        return msg;
    }

    public void messageReceived(NetworkMsgListenerContext networkMsgListenerContext, String msg) {
        logger.info("Received: {}", msg);
        MapInfo mapInfo = getMapInfoFrom(msg);
        logger.trace("Deserialize done.");

        stopListeningForMessages(networkMsgListenerContext);
        startProducingZombieEvents(mapInfo);
    }

    private MapInfo getMapInfoFrom(String msg) {
        String[] parts = getSecondWordIn(msg);
        return serializer.deserialize(parts[1], MapInfo.class);
    }

    private String[] getSecondWordIn(String msg) {
        return StringUtils.split(msg, " ", 2);
    }

    private void stopListeningForMessages(NetworkMsgListenerContext networkMsgListenerContext) {
        networkMsgListenerContext.setCurrentListener(new NoOpReceiver());
    }

    private void startProducingZombieEvents(MapInfo mapInfo) {
        Producer currentProducer = zombieMoverFactory.create(mapInfo);
        producerContext.setCurrentProducer(currentProducer);
        enqueueMessage(producerContext.nextMsg());
    }

}
