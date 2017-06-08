package com.yngvark.gridwalls.microservices.zombie.app;

public interface GameEventProducer {
    void produce();
    void stop();
}
