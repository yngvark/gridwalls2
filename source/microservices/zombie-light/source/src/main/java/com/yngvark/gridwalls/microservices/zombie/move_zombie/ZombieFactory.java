package com.yngvark.gridwalls.microservices.zombie.move_zombie;

import com.yngvark.gridwalls.microservices.zombie.common.MapInfo;

import java.util.Random;

public class ZombieFactory {
    public static Zombie create(MapInfo mapInfo, Random random) {
        return new ZombieController(new WanderingZombie(mapInfo, random,1, 1));
    }
}
