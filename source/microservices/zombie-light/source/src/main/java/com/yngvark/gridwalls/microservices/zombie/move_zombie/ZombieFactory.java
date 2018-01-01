package com.yngvark.gridwalls.microservices.zombie.move_zombie;

import com.yngvark.gridwalls.microservices.zombie.common.MapInfo;

public class ZombieFactory {
    public static Zombie create(MapInfo mapInfo) {
        return new ZombieController(new WanderingZombie(mapInfo, 1, 1));
    }
}
