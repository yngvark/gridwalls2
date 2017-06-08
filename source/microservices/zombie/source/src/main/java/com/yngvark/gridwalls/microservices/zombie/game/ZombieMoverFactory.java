package com.yngvark.gridwalls.microservices.zombie.game;

import com.yngvark.gridwalls.microservices.zombie.game.serialize_events.Serializer;

import java.util.Random;

class ZombieMoverFactory {
    private final Serializer serializer;
    private final Sleeper sleeper;
    private final Random random;

    public ZombieMoverFactory(Serializer serializer, Sleeper sleeper, Random random) {
        this.serializer = serializer;
        this.sleeper = sleeper;
        this.random = random;
    }

    public ZombieMover create(MapInfo mapInfo) {
        return new ZombieMover(
                serializer,
                sleeper,
                random,
                mapInfo);
    }
}
