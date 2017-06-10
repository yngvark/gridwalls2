package com.yngvark.gridwalls.microservices.zombie.game;

import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class ServerGreeter implements Producer {
    private final Logger logger = getLogger(getClass());

    private final MapInfoReceiver mapInfoReceiver;

    public ServerGreeter(MapInfoReceiver mapInfoReceiver) {
        this.mapInfoReceiver = mapInfoReceiver;
    }

    @Override
    public String nextMsg(ProducerContext producerContext) {
        producerContext.setCurrentProducer(mapInfoReceiver);
        return "/myNameIs zombie";
    }
}
