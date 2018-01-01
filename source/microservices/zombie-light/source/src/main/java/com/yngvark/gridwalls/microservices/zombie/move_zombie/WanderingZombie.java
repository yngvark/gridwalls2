package com.yngvark.gridwalls.microservices.zombie.move_zombie;

import com.yngvark.gridwalls.microservices.zombie.common.MapInfo;

import java.util.Random;

class WanderingZombie implements ZombieState {
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
        int toX = random.nextInt(mapInfo.getWidth()) + 1;
        int toY = random.nextInt(mapInfo.getHeight()) + 1;

        return new Move(toX, toY);
    }

    //    Zombie notice(ManMove manMove) {
//        return new ZombieThatHasNoticedAMan(manMove);
//    }

    // Attack attack()
}
