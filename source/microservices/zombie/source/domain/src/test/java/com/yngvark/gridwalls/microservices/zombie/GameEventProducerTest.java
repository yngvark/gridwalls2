package com.yngvark.gridwalls.microservices.zombie;

import com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs.get_map_info.MapInfo;
import com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs.move.Move;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.slf4j.LoggerFactory.getLogger;

public class GameEventProducerTest {
    private final Logger logger = getLogger(getClass());

    @Test
    public void first_output_should_be_subscribe_to_mapinfo() {
        // Given
        TestHelper testHelper = new TestHelper();

        // When
        String msg = testHelper.game.produceNext();

        // Then
        assertEquals("/subscribeTo MapInfo", msg);
    }

    @Test
    public void should_wait_for_map_info_before_moving()
            throws TimeoutException, ExecutionException, InterruptedException {
        // Given
        TestHelper testHelper = new TestHelper();
        testHelper.readAndAssertSubscription();

        ExecutorService executorService = Executors.newCachedThreadPool();

        // When
        Future nextMsgFuture = executorService.submit(() -> testHelper.game.produceNext());

        // Then
        assertThrows(TimeoutException.class, () -> nextMsgFuture.get(300, TimeUnit.MILLISECONDS));

        // And when
        testHelper.messageReceived("MapInfo", new MapInfo(3, 5));
        String nextMsg = testHelper.game.produceNext();

        // Then
        // Check validity of move by deserializing the string
        testHelper.deserializePublish(nextMsg, Move.class);
    }

    @Test
    public void should_move_within_map() {
        TestHelper testHelper = new TestHelper();
        testHelper.readAndAssertSubscription();

        MapInfo mapInfo = new MapInfo(10, 7);
        testHelper.messageReceived("MapInfo", mapInfo);

        // When
        for (int i = 0; i < 1; i++) {
            String msg = testHelper.game.produceNext();
            Move move = testHelper.deserializePublish(msg, Move.class);
            gatherMinMax(move);

            // Then
            assertTrue(isWithinMap(move, mapInfo));
        }

        // Then
//        assertEquals(new Integer(1), minX);
//        assertEquals(new Integer(10), maxX);
//
//        assertEquals(new Integer(1), minY);
//        assertEquals(new Integer(7), maxY);
    }

    private boolean isWithinMap(Move move, MapInfo mapInfo) {
        return move.toX >= 1
                && move.toX <= mapInfo.width
                && move.toY >= 1
                && move.toY <= mapInfo.height;
    }

    private Integer maxX = null , maxY = null, minX = null, minY = null;
    private void gatherMinMax(Move move) {
        minX = minX == null ? move.toX : Math.min(minX, move.toX);
        maxX = maxX == null ? move.toX : Math.max(maxX, move.toX);

        minY = minY == null ? move.toY : Math.min(minY, move.toY);
        maxY = maxY == null ? move.toY : Math.max(maxY, move.toY);
    }

    @Test
    public void should_sleep_between_100_and_1000_ticks_between_moves() {
        // Given
        TestHelper testHelper = new TestHelper();
        testHelper.readAndAssertSubscription();

        testHelper.messageReceived("MapInfo", new MapInfo(10, 7));

        // When
        for (int i = 0; i < 1; i++) {
            testHelper.game.produceNext();

            // Then
//            assertTrue(
//                    testHelper.testSleeper.lastSleepDurationWasBetweenInclusive(100, 1000),
//                    testHelper.testSleeper.toString());
        }
    }
}