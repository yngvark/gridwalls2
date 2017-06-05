package com.yngvark.gridwalls.microservices.zombie.game;

import com.yngvark.gridwalls.microservices.zombie.game.move.Move;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.slf4j.LoggerFactory.getLogger;

public class GameLogicTest {
    private final Logger logger = getLogger(getClass());

    @Test
    public void should_move_within_map() {
        // Given
        TestSleeper testSleeper = new TestSleeper();
        Serializer serializer = new JsonSerializer();
        GameLogic gameLogic = new GameLogic(
                testSleeper,
                serializer,
                new Random(98161));

        MapInfo mapInfo = new MapInfo(10, 7);
        String mapInfoStr = serializer.serialize(mapInfo, MapInfo.class);
        gameLogic.messageReceived(mapInfoStr);

        // When
        for (int i = 0; i < 1000; i++) {
            String msg = gameLogic.nextMsg();
            Move move = serializer.deserialize(msg, Move.class);
            // Then
            assertTrue(isWithinMap(move, mapInfo));
            gatherMinMax(move);
        }

        // Then
        assertEquals(new Integer(1), minX);
        assertEquals(new Integer(10), maxX);

        assertEquals(new Integer(1), minY);
        assertEquals(new Integer(7), maxY);
    }

    Integer maxX = null , maxY = null, minX = null, minY = null;
    private void gatherMinMax(Move move) {
        minX = minX == null ? move.toX : Math.min(minX, move.toX);
        maxX = maxX == null ? move.toX : Math.max(maxX, move.toX);

        minY = minY == null ? move.toY : Math.min(minY, move.toY);
        maxY = maxY == null ? move.toY : Math.max(maxY, move.toY);
    }



    private boolean isWithinMap(Move move, MapInfo mapInfo) {
        return move.toX >= 1
                && move.toX <= mapInfo.width
                && move.toY >= 1
                && move.toY <= mapInfo.height;
    }


    @Test
    public void should_sleep_between_100_and_1000_ticks_between_moves() {
        // Given
        TestSleeper testSleeper = new TestSleeper();
        Serializer serializer = new JsonSerializer();
        GameLogic gameLogic = new GameLogic(
                testSleeper,
                serializer,
                new Random(98161));

        MapInfo mapInfo = new MapInfo(10, 7);
        String mapInfoStr = serializer.serialize(mapInfo, MapInfo.class);
        gameLogic.messageReceived(mapInfoStr);

        // When
        for (int i = 0; i < 1000; i++) {
            gameLogic.nextMsg();
            assertTrue(
                    testSleeper.lastSleepDurationWasBetweenInclusive(100, 1000),
                    testSleeper.toString());
        }
    }

}