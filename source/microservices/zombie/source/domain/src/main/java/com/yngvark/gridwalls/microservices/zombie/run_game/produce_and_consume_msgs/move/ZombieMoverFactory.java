package com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs.move;

import com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs.get_map_info.MapInfo;
import com.yngvark.gridwalls.microservices.zombie.run_game.Sleeper;
import com.yngvark.gridwalls.microservices.zombie.run_game.serialize_msgs.Serializer;

import java.util.Random;

public class ZombieMoverFactory {
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
