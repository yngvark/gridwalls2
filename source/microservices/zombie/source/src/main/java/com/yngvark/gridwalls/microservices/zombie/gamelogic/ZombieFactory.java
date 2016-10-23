package com.yngvark.gridwalls.microservices.zombie.gamelogic;

import com.yngvark.gridwalls.core.CoordinateFactory;
import com.yngvark.gridwalls.core.MapCoordinates;
import com.yngvark.gridwalls.core.MapDimensions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ZombieFactory {
    public List<Zombie> createZombies(MapDimensions mapDimensions) {
        List<Zombie> zombies = new ArrayList<>();
        MapCoordinates mapCoordinates = new MapCoordinates(mapDimensions);
        CoordinateFactory coordinateFactory = new CoordinateFactory(mapCoordinates);

        Zombie zombie1 = new Zombie(coordinateFactory, UUID.randomUUID(), mapCoordinates.center());
        Zombie zombie2 = new Zombie(coordinateFactory, UUID.randomUUID(), mapCoordinates.center());

        zombies.add(zombie1);
        zombies.add(zombie2);

        return zombies;
    }
}
