package com.yngvark.gridwalls.microservices.zombie;

import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.gridwalls.microservices.zombie.run_game.GameFactory;
import com.yngvark.gridwalls.microservices.zombie.run_game.NetworkMessageListener;
import com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs.BlockingGameEventProducer;
import com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs.move.Move;
import com.yngvark.gridwalls.microservices.zombie.run_game.serialize_msgs.Serializer;
import org.apache.commons.lang3.StringUtils;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class TestHelper {
    TestSleeper testSleeper = new TestSleeper();
    Serializer serializer = new TestSerializer();

    GameFactory gameFactory = GameFactory.create(testSleeper, new Random(12345), serializer);
    NetworkMessageListener networkMessageListener = gameFactory.createNetworkMessageListener();
    OutputFileWriter outputFileWriter = mock(OutputFileWriter.class);
    BlockingGameEventProducer game = (BlockingGameEventProducer) gameFactory.create(outputFileWriter);

    void messageReceived(String topic, Object event) {
        String serializedEvent = serializer.serialize(event);
        networkMessageListener.messageReceived("[" + topic + "] " + serializedEvent);
    }

    public Move deserializePublish(String msg, Class<Move> clazz) {
        assertTrue(msg.startsWith("/publishTo Zombie "));

        String[] parts = StringUtils.split(msg, " ", 3);
        return serializer.deserialize(parts[2], clazz);
    }

    public void readAndAssertSubscription() {
        assertEquals("/subscribeTo MapInfo", game.produceNext());
    }
}