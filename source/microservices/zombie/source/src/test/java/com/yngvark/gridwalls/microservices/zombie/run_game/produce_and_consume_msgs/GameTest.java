package com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs;

import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.gridwalls.microservices.zombie.run_app.NetworkMessageListener;
import com.yngvark.gridwalls.microservices.zombie.run_game.GameFactory;
import com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs.get_map_info.MapInfo;
import com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs.move.Move;
import com.yngvark.gridwalls.microservices.zombie.run_game.serialize_msgs.JsonSerializer;
import com.yngvark.gridwalls.microservices.zombie.run_game.serialize_msgs.Serializer;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.io.IOException;
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
        OutputFileWriter outputFileWriter = mock(OutputFileWriter.class);
        BlockingGameEventProducer gameEventProducer = (BlockingGameEventProducer)
                gameFactory.createEventProducer(outputFileWriter);
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

        public void readAndAssertFirstMsg() {
            assertEquals("/myNameIs zombie", gameEventProducer.nextMsg());
        }

        public void readAndAssertSecondMsg() {
            assertEquals("/subscribeTo MapInfo", gameEventProducer.nextMsg());
        }

        public String nextMsg() {
            return assertTimeoutPreemptively(ofMillis(300), () -> gameEventProducer.nextMsg());
        }
    }

    @Test
    public void should_start_by_greeting_serrver() throws IOException {
        TestHelper testHelper = new TestHelper();

        // When
        String msg = testHelper.nextMsg();

        // Then
        assertEquals("/myNameIs zombie", msg);
    }

    @Test
    public void should_subscribe_to_mapinfo_after_greeting_server() {
        TestHelper testHelper = new TestHelper();
        testHelper.readAndAssertFirstMsg();

        // When
        String msg = testHelper.nextMsg();

        // Then
        assertEquals("/subscribeTo MapInfo", msg);
    }

    @Test
    public void should_wait_for_map_info_before_moving()
            throws TimeoutException, ExecutionException, InterruptedException {
        // Given
        TestHelper testHelper = new TestHelper();
        testHelper.readAndAssertFirstMsg();
        testHelper.readAndAssertSecondMsg();

        ExecutorService executorService = Executors.newCachedThreadPool();
        Future nextMsgFuture = executorService.submit(() -> testHelper.gameEventProducer.nextMsg());
        assertThrows(TimeoutException.class, () -> nextMsgFuture.get(300, TimeUnit.MILLISECONDS));

        // When
        testHelper.messageReceived(new MapInfo(3, 5));

        // Then
        String nextMsg = testHelper.nextMsg();
        // Check validity of move by deserializing the string
        testHelper.deserializePublish(nextMsg, Move.class);
    }

    @Test
    public void should_move_within_map() {
        TestHelper testHelper = new TestHelper();
        testHelper.readAndAssertFirstMsg();
        testHelper.readAndAssertSecondMsg();

        MapInfo mapInfo = new MapInfo(10, 7);
        testHelper.messageReceived(mapInfo);

        // When
        for (int i = 0; i < 1000; i++) {
            String msg = testHelper.gameEventProducer.nextMsg();
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
        testHelper.readAndAssertFirstMsg();
        testHelper.readAndAssertSecondMsg();

        testHelper.messageReceived(new MapInfo(10, 7));

        // When
        for (int i = 0; i < 1000; i++) {
            testHelper.gameEventProducer.nextMsg();

            // Then
            assertTrue(
                    testHelper.testSleeper.lastSleepDurationWasBetweenInclusive(100, 1000),
                    testHelper.testSleeper.toString());
        }
    }
}