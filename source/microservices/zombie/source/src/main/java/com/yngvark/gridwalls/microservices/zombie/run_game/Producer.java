package com.yngvark.gridwalls.microservices.zombie.run_game;

public interface Producer {
    String nextMsg(ProducerContext producerContext);
}
