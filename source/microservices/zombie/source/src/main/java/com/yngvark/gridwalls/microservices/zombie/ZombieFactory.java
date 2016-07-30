package com.yngvark.gridwalls.microservices.zombie;

import com.yngvark.gridwalls.core.CoordinateFactory;
import com.yngvark.gridwalls.core.MapCoordinates;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class ZombieFactory {
    public List<Zombie> createZombies(int mapHeight, int mapWidth) {
        List<Zombie> zombies = new ArrayList<>();
        MapCoordinates mapCoordinates = new MapCoordinates(mapHeight, mapWidth);
        CoordinateFactory coordinateFactory = new CoordinateFactory(mapCoordinates);

        Zombie zombie1 = new Zombie(coordinateFactory, UUID.randomUUID(), mapCoordinates.center());
        Zombie zombie2 = new Zombie(coordinateFactory, UUID.randomUUID(), mapCoordinates.center());

        zombies.add(zombie1);
        zombies.add(zombie2);

        return zombies;
    }
}
