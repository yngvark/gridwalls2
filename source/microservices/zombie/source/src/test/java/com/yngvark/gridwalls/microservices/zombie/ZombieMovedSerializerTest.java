package com.yngvark.gridwalls.microservices.zombie;

import com.yngvark.gridwalls.core.Coordinate;
import com.yngvark.gridwalls.core.CoordinateSerializer;
import com.yngvark.gridwalls.microservices.zombie.game.ZombieMoved;
import com.yngvark.gridwalls.microservices.zombie.game.ZombieMovedSerializer;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ZombieMovedSerializerTest {
    @Test
    public void deserialize() throws Exception {
        ZombieMovedSerializer zombieMovedSerializer = new ZombieMovedSerializer(new CoordinateSerializer());
        ZombieMoved zombieMoved = new ZombieMoved(UUID.randomUUID(), new Coordinate(2, 5));

        // When
        String serialized = zombieMovedSerializer.serialize(zombieMoved);

        // Then
        assertEquals(zombieMoved, zombieMovedSerializer.deserialize(serialized));
    }
}