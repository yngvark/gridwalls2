package com.yngvark.gridwalls.microservices.zombie.game;

import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.gridwalls.microservices.zombie.app.NetworkMessageListener;
import com.yngvark.gridwalls.microservices.zombie.game.move.Move;
import com.yngvark.gridwalls.microservices.zombie.game.serialize_events.JsonSerializer;
import com.yngvark.gridwalls.microservices.zombie.game.serialize_events.Serializer;
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
import static org.mockito.Mockito.mock;
import static org.slf4j.LoggerFactory.getLogger;

public class GameEventProducerLogicSelectorSelectorTest {
    private final Logger logger = getLogger(getClass());

    class TestRun {
        GameTestFactory gameFactory = GameTestFactory.create();
        NetworkMessageListener networkMessageListener = gameFactory.createNetworkMessageListener();
        BlockingGameEventProducer gameEventProducer = gameFactory.createEventProducer(mock(OutputFileWriter.class));
        Serializer serializer = new JsonSerializer();

        void messageReceived(Object event) {
            String eventStr = serializer.serialize(event);
            networkMessageListener.messageReceived(eventStr);
        }
    }

    @Test
    public void should_move_within_map() {
        TestRun testRun = new TestRun();

        MapInfo mapInfo = new MapInfo(10, 7);
        testRun.messageReceived(mapInfo);

        // When
        for (int i = 0; i < 1000; i++) {
            String msg = testRun.gameEventProducer.produceOne();
            Move move = testRun.serializer.deserialize(msg, Move.class);
            gatherMinMax(move);

            // Then
            assertTrue(isWithinMap(move, mapInfo));
        }

        // Then
        assertEquals(new Integer(1), minX);
        assertEquals(new Integer(10), maxX);

        assertEquals(new Integer(1), minY);
        assertEquals(new Integer(7), maxY);
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
        TestRun testRun = new TestRun();

        testRun.messageReceived(new MapInfo(10, 7));

        // When
        for (int i = 0; i < 1000; i++) {
            testRun.gameEventProducer.produceOne();
            assertTrue(
                    testRun.gameFactory.testSleeper.lastSleepDurationWasBetweenInclusive(100, 1000),
                    testRun.gameFactory.testSleeper.toString());
        }
    }

    @Test
    public void should_wait_for_map_info_before_moving()
            throws TimeoutException, ExecutionException, InterruptedException {
        // Given
        TestRun testRun = new TestRun();

        ExecutorService executorService = Executors.newCachedThreadPool();
        Future nextMsgFuture = executorService.submit(() -> testRun.gameEventProducer.produceOne());
        assertThrows(TimeoutException.class, () -> nextMsgFuture.get(300, TimeUnit.MILLISECONDS));

        // When
        testRun.messageReceived(new MapInfo(3, 5));

        // Then
        String nextMsg = testRun.gameEventProducer.produceOne();
        // Check validity of move by deserializing the string
        testRun.serializer.deserialize(nextMsg, Move.class);
    }
}