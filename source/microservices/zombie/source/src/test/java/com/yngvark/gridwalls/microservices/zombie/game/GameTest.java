package com.yngvark.gridwalls.microservices.zombie.game;

import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.gridwalls.microservices.zombie.app.NetworkMessageListener;
import com.yngvark.gridwalls.microservices.zombie.game.move.Move;
import com.yngvark.gridwalls.microservices.zombie.game.serialize_events.JsonSerializer;
import com.yngvark.gridwalls.microservices.zombie.game.serialize_events.Serializer;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.time.Duration.ofMillis;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.slf4j.LoggerFactory.getLogger;

public class GameTest {
    private final Logger logger = getLogger(getClass());

    class TestHelper {
        TestSleeper testSleeper = new TestSleeper();
        GameFactory gameFactory = GameFactory.create(testSleeper, new Random(12345));
        NetworkMessageListener networkMessageListener = gameFactory.createNetworkMessageListener();
        BlockingGameEventProducer gameEventProducer = (BlockingGameEventProducer)
                gameFactory.createEventProducer(mock(OutputFileWriter.class));
        Serializer serializer = new JsonSerializer();

        void messageReceived(Object event) {
            String eventStr = serializer.serialize(event);
            networkMessageListener.messageReceived(eventStr);
        }

        public Move deserializePublish(String msg, Class<Move> clazz) {
            String[] parts = StringUtils.split(msg, " ", 2);
            assertEquals("/publish", parts[0]);

            return serializer.deserialize(parts[1], clazz);
        }

        public void assertThatFirstProducedMessageIsGreeting() {
            assertEquals("/myNameIs zombie", gameEventProducer.produceOne());
        }
    }

    @Test
    public void should_start_by_greeting_serrver() {
        TestHelper testHelper = new TestHelper();

        // When
        String msg = testHelper.gameEventProducer.produceOne();

        // Then
        assertEquals("/myNameIs zombie", msg);
    }

    @Test
    public void should_subscribe_to_mapinfo_after_greeting_server() {
        TestHelper testHelper = new TestHelper();
        testHelper.assertThatFirstProducedMessageIsGreeting();

        // When
        String msg = assertTimeoutPreemptively(ofMillis(300), () -> testHelper.gameEventProducer.produceOne());

        // Then
        assertEquals("/subscribeTo MapInfo", msg);
    }

    @Test
    public void should_wait_for_map_info_before_moving()
            throws TimeoutException, ExecutionException, InterruptedException {
        // Given
        TestHelper testHelper = new TestHelper();
        testHelper.assertThatFirstProducedMessageIsGreeting();

        ExecutorService executorService = Executors.newCachedThreadPool();
        Future nextMsgFuture = executorService.submit(() -> testHelper.gameEventProducer.produceOne());
        assertThrows(TimeoutException.class, () -> nextMsgFuture.get(300, TimeUnit.MILLISECONDS));

        // When
        testHelper.messageReceived(new MapInfo(3, 5));

        // Then
        String nextMsg = testHelper.gameEventProducer.produceOne();
        // Check validity of move by deserializing the string
        testHelper.deserializePublish(nextMsg, Move.class);
    }

    @Test
    public void should_move_within_map() {
        TestHelper testHelper = new TestHelper();
        testHelper.assertThatFirstProducedMessageIsGreeting();

        MapInfo mapInfo = new MapInfo(10, 7);
        testHelper.messageReceived(mapInfo);

        // When
        for (int i = 0; i < 1000; i++) {
            String msg = testHelper.gameEventProducer.produceOne();
            Move move = testHelper.deserializePublish(msg, Move.class);
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
        TestHelper testHelper = new TestHelper();
        testHelper.assertThatFirstProducedMessageIsGreeting();

        testHelper.messageReceived(new MapInfo(10, 7));

        // When
        for (int i = 0; i < 1000; i++) {
            testHelper.gameEventProducer.produceOne();
            assertTrue(
                    testHelper.testSleeper.lastSleepDurationWasBetweenInclusive(100, 1000),
                    testHelper.testSleeper.toString());
        }
    }
}