package com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs.greet_server;

import com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs.Producer;
import com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs.ProducerContext;

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
