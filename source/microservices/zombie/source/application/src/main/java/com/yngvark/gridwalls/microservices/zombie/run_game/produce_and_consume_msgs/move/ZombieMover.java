package com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs.move;

import com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs.get_map_info.MapInfo;
import com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs.Producer;
import com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs.ProducerContext;
import com.yngvark.gridwalls.microservices.zombie.run_game.Sleeper;
import com.yngvark.gridwalls.microservices.zombie.run_game.serialize_msgs.Serializer;
import org.slf4j.Logger;

import java.util.Random;

import static org.slf4j.LoggerFactory.getLogger;

class ZombieMover implements Producer {
    private final Logger logger = getLogger(getClass());
    private final Serializer serializer;
    private final Sleeper sleeper;
    private final Random random;
    private final MapInfo mapInfo;

    public ZombieMover(Serializer serializer, Sleeper sleeper, Random random,
            MapInfo mapInfo) {
        this.serializer = serializer;
        this.sleeper = sleeper;
        this.random = random;
        this.mapInfo = mapInfo;
    }

    @Override
    public String nextMsg(ProducerContext producerContext) {
        sleeper.sleep(100 + random.nextInt(901));
        Move move = getNextMove();
        return "/publishTo Zombie " + serializer.serialize(move, Move.class);
    }

    private Move getNextMove() {
        int toX = random.nextInt(mapInfo.width) + 1;
        int toY = random.nextInt(mapInfo.height) + 1;
        return new Move(toX, toY);
    }
}
