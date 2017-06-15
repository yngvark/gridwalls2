package com.yngvark.gridwalls.microservices.zombie.run_game;

import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.gridwalls.microservices.zombie.run_app.GameEventProducer;
import com.yngvark.gridwalls.microservices.zombie.run_app.NetworkMessageListener;
import com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs.BlockingGameEventProducer;
import com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs.NetworkMsgListener;
import com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs.NetworkMsgListenerContext;
import com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs.Producer;
import com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs.ProducerContext;
import com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs.greet_server.ServerGreeter;
import com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs.get_map_info.MapInfoReceiver;
import com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs.move.ZombieMoverFactory;
import com.yngvark.gridwalls.microservices.zombie.run_game.serialize_msgs.JsonSerializer;
import com.yngvark.gridwalls.microservices.zombie.run_game.serialize_msgs.Serializer;

import java.util.Random;

public class GameFactory {
    private final NetworkMsgListener networkMsgListener;
    private final Producer producer;

    public static GameFactory create() {
        return GameFactory.create(new ThreadSleeper(), new Random());
    }

    public static GameFactory create(Sleeper sleeper, Random random) {
        Serializer serializer = new JsonSerializer();

        MapInfoReceiver mapInfoReceiver = new MapInfoReceiver(
                serializer,
                new ZombieMoverFactory(
                        serializer,
                        sleeper,
                        random
                )
        );

        ServerGreeter serverGreeter = new ServerGreeter(mapInfoReceiver);

        return new GameFactory(mapInfoReceiver, serverGreeter);
    }


    public GameFactory(
            NetworkMsgListener networkMsgListener,
            Producer producer) {
        this.networkMsgListener = networkMsgListener;
        this.producer = producer;
    }

    public NetworkMessageListener createNetworkMessageListener() {
        return new NetworkMsgListenerContext(networkMsgListener);
    }

    public GameEventProducer createEventProducer(OutputFileWriter outputFileWriter) {
        return new BlockingGameEventProducer(
                outputFileWriter,
                new ProducerContext(producer)
        );
    }

}
