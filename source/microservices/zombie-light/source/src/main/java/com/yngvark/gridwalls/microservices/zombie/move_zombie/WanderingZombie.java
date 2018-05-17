package com.yngvark.gridwalls.microservices.zombie.move_zombie;

import com.yngvark.gridwalls.microservices.zombie.common.MapInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

class WanderingZombie implements ZombieState {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final MapInfo mapInfo;
    private final Random random;

    private final int x;
    private final int y;

    public WanderingZombie(MapInfo mapInfo, Random random, int x, int y) {
        this.mapInfo = mapInfo;
        this.random = random;
        this.x = x;
        this.y = y;
    }

    public Container move() {
        Move move = getMove();

        return new Container(
                move,
                new WanderingZombie(mapInfo, random, move.getX(), move.getY())
        );
    }

    private Move getMove() {
        int toX = -1;
        while (toX < 0 || toX >= mapInfo.getWidth()) {
            int xDiff = random.nextInt(3) - 1;
            toX = x + xDiff;
        }

        int toY = -1;
        while (toY < 0 || toY >= mapInfo.getHeight()) {
            int yDiff = random.nextInt(3) - 1;
            toY = y + yDiff;
        }

        return new Move(toX, toY);
    }

    //    Zombie notice(ManMove manMove) {
//        return new ZombieThatHasNoticedAMan(manMove);
//    }

    // Attack attack()
}
