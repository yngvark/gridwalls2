package com.yngvark.gridwalls.microservices.zombie.game;

import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.gridwalls.microservices.zombie.app.NetworkMessageListener;
import com.yngvark.gridwalls.microservices.zombie.game.serialize_events.JsonSerializer;
import com.yngvark.gridwalls.microservices.zombie.game.serialize_events.Serializer;

import java.util.Random;

public class GameTestFactory {
    final MapInfoReceiver mapInfoReceiver;
    final TestSleeper testSleeper;

    public static GameTestFactory create() {
        Serializer serializer = new JsonSerializer();

        TestSleeper testSleeper = new TestSleeper();
        MapInfoReceiver mapInfoReceiver = new MapInfoReceiver(
                serializer,
                new ZombieMoverFactory(
                        serializer,
                        testSleeper,
                        new Random(12345)
                )
        );

        return new GameTestFactory(mapInfoReceiver, testSleeper);
    }

    public GameTestFactory(MapInfoReceiver mapInfoReceiver,
            TestSleeper testSleeper) {
        this.mapInfoReceiver = mapInfoReceiver;
        this.testSleeper = testSleeper;
    }

    public NetworkMessageListener createNetworkMessageListener() {
        return new NetwMsgReceiverContext(mapInfoReceiver);
    }

    public BlockingGameEventProducer createEventProducer(OutputFileWriter outputFileWriter) {
        return new BlockingGameEventProducer(
                outputFileWriter,
                new GameLogicContext(mapInfoReceiver)
        );
    }
}
