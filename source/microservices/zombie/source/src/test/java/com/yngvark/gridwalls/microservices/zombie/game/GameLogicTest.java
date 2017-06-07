package com.yngvark.gridwalls.microservices.zombie.game;

import com.yngvark.gridwalls.microservices.zombie.game.move.Move;
import com.yngvark.gridwalls.microservices.zombie.game.serialize_events.JsonSerializer;
import com.yngvark.gridwalls.microservices.zombie.game.serialize_events.Serializer;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.lang.reflect.GenericArrayType;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.slf4j.LoggerFactory.getLogger;

public class GameLogicTest {
    private final Logger logger = getLogger(getClass());

    class GameLogicBuilder {
        TestSleeper testSleeper = new TestSleeper();
        Serializer serializer = new JsonSerializer();
        GameLogic gameLogic = new GameLogic(
                testSleeper,
                serializer,
                new Random(98161));

    }

    @Test
    public void should_move_within_map() {
        // Given
        GameLogicBuilder gameLogicBuilder = new GameLogicBuilder();
        Serializer serializer = gameLogicBuilder.serializer;
        GameLogic gameLogic = gameLogicBuilder.gameLogic;

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
        GameLogicBuilder gameLogicBuilder = new GameLogicBuilder();
        Serializer serializer = gameLogicBuilder.serializer;
        TestSleeper testSleeper = gameLogicBuilder.testSleeper;
        GameLogic gameLogic = gameLogicBuilder.gameLogic;

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

    @Test
    public void should_wait_for_map_info_before_moving()
            throws TimeoutException, ExecutionException, InterruptedException {
        // Given
        GameLogicBuilder gameLogicBuilder = new GameLogicBuilder();
        GameLogic gameLogic = gameLogicBuilder.gameLogic;

        ExecutorService executorService = Executors.newCachedThreadPool();
        Future nextMsgFuture = executorService.submit(() -> gameLogic.nextMsg());
        assertThrows(TimeoutException.class, () -> nextMsgFuture.get(300, TimeUnit.MILLISECONDS));

        // When
        String mapInfo = gameLogicBuilder.serializer.serialize(new MapInfo(3, 5), MapInfo.class);
        gameLogic.messageReceived(mapInfo);

        // Then
        String nextMsg = gameLogic.nextMsg();
        // Check validity of move by deserializing the string
        gameLogicBuilder.serializer.deserialize(nextMsg, Move.class);
    }
}