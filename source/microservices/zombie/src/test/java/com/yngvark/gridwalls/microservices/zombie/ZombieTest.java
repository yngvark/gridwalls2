package com.yngvark.gridwalls.microservices.zombie;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

public class ZombieTest {
    @Test
    public void should_always_change_coordinate_and_never_stand_still_on_next_turn() {
        // Given
        TestNetCom netCom = TestNetComFactory.create();

        MapCoordinates mapCoordinates = new MapCoordinates(10, 10);
        CoordinateFactory coordinateFactory = new CoordinateFactory(mapCoordinates);

        ZombieMovedSerializer zombieMovedSerializer = new ZombieMovedSerializer(new CoordinateSerializer());

        Coordinate initCoord = mapCoordinates.middle();
        Zombie zombie = new Zombie(netCom, coordinateFactory, zombieMovedSerializer, initCoord);

        Coordinate lastCoord = initCoord;
        for (int i = 0; i < 1000; i++) {
            // When
            zombie.nextTurn();

            // Then
            ZombieMoved zombieMoved = zombieMovedSerializer.deserialize(netCom.consume());

            assertNotEquals(lastCoord, zombieMoved.getTargetCoordinate());
            assertFalse(netCom.hasMoreEvents());

            // Finally
            lastCoord = zombieMoved.getTargetCoordinate();
        }
    }

    @Test
    public void should_visit_all_coords_of_a_small_map() {
        // Given
        TestNetCom netCom = TestNetComFactory.create();

        MapCoordinates mapCoordinates = new MapCoordinates(2, 2);
        CoordinateFactory coordinateFactory = new CoordinateFactory(mapCoordinates);

        ZombieMovedSerializer zombieMovedSerializer = new ZombieMovedSerializer(new CoordinateSerializer());

        Coordinate initCoord = mapCoordinates.middle();
        Zombie zombie = new Zombie(netCom, coordinateFactory, zombieMovedSerializer, initCoord);

        Set<Coordinate> visitedCoords = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            // When
            zombie.nextTurn();

            // Record result
            ZombieMoved zombieMoved = zombieMovedSerializer.deserialize(netCom.consume());
            visitedCoords.add(zombieMoved.getTargetCoordinate());
        }

        // Then
        assertEquals(mapCoordinates.allCoordinates(), visitedCoords);
    }
}
