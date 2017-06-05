package com.yngvark.gridwalls.microservices.zombie.game;

import com.yngvark.gridwalls.microservices.zombie.game.move.Move;

import java.util.Random;

class GameLogic {
    private final Sleeper sleeper;
    private final Serializer serializer;
    private final Random random;

    private MapInfo mapInfo;

    public GameLogic(Sleeper sleeper, Serializer serializer, Random random) {
        this.sleeper = sleeper;
        this.serializer = serializer;
        this.random = random;
    }

    public String nextMsg() {
        sleeper.sleep(100 + random.nextInt(901));
        return serializer.serialize(getNextMove(), Move.class);
    }

    private Move getNextMove() {
        int toX = random.nextInt(mapInfo.width) + 1;
        int toY = random.nextInt(mapInfo.height) + 1;
        return new Move(toX, toY);
    }

    public void messageReceived(String msg) {
        mapInfo = serializer.deserialize(msg, MapInfo.class);
    }
}
