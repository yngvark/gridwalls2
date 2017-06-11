package com.yngvark.gridwalls.microservices.zombie.run_game.get_map_info;

import com.yngvark.gridwalls.microservices.zombie.run_game.NetworkMsgListener;
import com.yngvark.gridwalls.microservices.zombie.run_game.NetworkMsgListenerContext;
import com.yngvark.gridwalls.microservices.zombie.run_game.Producer;
import com.yngvark.gridwalls.microservices.zombie.run_game.ProducerContext;
import com.yngvark.gridwalls.microservices.zombie.run_game.move.ZombieMoverFactory;
import com.yngvark.gridwalls.microservices.zombie.run_game.serialize_events.Serializer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MapInfoReceiver implements Producer, NetworkMsgListener {
    private final Serializer serializer;
    private final ZombieMoverFactory zombieMoverFactory;

    private BlockingQueue<String> messages = new LinkedBlockingQueue<>();
    private ProducerContext producerContext;

    public MapInfoReceiver(Serializer serializer,
            ZombieMoverFactory zombieMoverFactory) {
        this.serializer = serializer;
        this.zombieMoverFactory = zombieMoverFactory;
        produce("/subscribeTo MapInfo");
    }

    private void produce(String msg) {
        try {
            messages.put(msg);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String nextMsg(ProducerContext producerContext) {
        try {
            this.producerContext = producerContext;
            return messages.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void messageReceived(NetworkMsgListenerContext networkMsgListenerContext, String msg) {
        MapInfo mapInfo = serializer.deserialize(msg, MapInfo.class);
        networkMsgListenerContext.setCurrentListener(new NoOpReceiver());

        producerContext.setCurrentProducer(zombieMoverFactory.create(mapInfo));
        produce(producerContext.nextMsg());
    }

}
