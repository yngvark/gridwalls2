package com.yngvark.gridwalls.microservices.zombie.move_zombie;

import com.yngvark.gridwalls.microservices.zombie.common.MapInfo;

class WanderingZombie implements ZombieState {
    private final MapInfo mapInfo;
    private final int x;
    private final int y;

    public WanderingZombie(MapInfo mapInfo, int x, int y) {
        this.mapInfo = mapInfo;
        this.x = x;
        this.y = y;
    }

    public Container move() {
        return new Container(
                new Move(1, 1),
                new WanderingZombie(mapInfo, 1, 1)
        );
    }

//    Zombie notice(ManMove manMove) {
//        return new ZombieThatHasNoticedAMan(manMove);
//    }

    // Attack attack()
}
