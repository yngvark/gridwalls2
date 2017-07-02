package com.yngvark.gridwalls.microservices.zombie.run_game;

public interface GameEventProducer {
    void produce();
    void stop();
}
