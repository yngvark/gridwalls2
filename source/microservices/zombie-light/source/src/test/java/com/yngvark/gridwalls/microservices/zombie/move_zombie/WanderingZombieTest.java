package com.yngvark.gridwalls.microservices.zombie.move_zombie;

import com.yngvark.gridwalls.microservices.zombie.common.MapInfo;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class WanderingZombieTest {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    void should_move_within_map() {
        // Given
        WanderingZombie zombie = new WanderingZombie(
                new MapInfo(10, 7),
                new Random(123),
                0, 0
                );

        // When
        for (int i = 0; i < 1000; i++) {
            Container container = zombie.move();
            Move m = container.move;
            logger.debug(m.toString());

            // Then
            assertTrue(m.getX() <= 10 && m.getX() >= 0
                            && m.getY() <= 7 && m.getY() >= 0,
                    "x: " + m.getX() + ", y: " + m.getY());
        }
    }
}
