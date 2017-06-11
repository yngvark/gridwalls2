package com.yngvark.gridwalls.microservices.zombie.run_game.greet_server;

import com.yngvark.gridwalls.microservices.zombie.run_game.Producer;
import com.yngvark.gridwalls.microservices.zombie.run_game.ProducerContext;

public class ServerGreeter implements Producer {
    private final Producer nextProducer;

    public ServerGreeter(Producer nextProducer) {
        this.nextProducer = nextProducer;
    }

    @Override
    public String nextMsg(ProducerContext producerContext) {
        producerContext.setCurrentProducer(nextProducer);
        return "/myNameIs zombie";
    }
}
