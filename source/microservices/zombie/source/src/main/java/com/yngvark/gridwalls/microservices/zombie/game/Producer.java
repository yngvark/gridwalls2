package com.yngvark.gridwalls.microservices.zombie.game;

interface Producer {
    String nextMsg(ProducerContext producerContext);
}
