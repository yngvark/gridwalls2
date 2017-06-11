package com.yngvark.gridwalls.microservices.zombie.run_game;

import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.gridwalls.microservices.zombie.run_app.GameEventProducer;
import com.yngvark.gridwalls.microservices.zombie.run_app.NetworkMessageListener;
import com.yngvark.gridwalls.microservices.zombie.run_game.greet_server.ServerGreeter;
import com.yngvark.gridwalls.microservices.zombie.run_game.get_map_info.MapInfoReceiver;
import com.yngvark.gridwalls.microservices.zombie.run_game.move.ZombieMoverFactory;
import com.yngvark.gridwalls.microservices.zombie.run_game.serialize_events.JsonSerializer;
import com.yngvark.gridwalls.microservices.zombie.run_game.serialize_events.Serializer;

import java.util.Random;

public class GameFactory {
    private final MapInfoReceiver mapInfoReceiver;
    private final ServerGreeter serverGreeter;

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


    public GameFactory(MapInfoReceiver mapInfoReceiver,
            ServerGreeter serverGreeter) {
        this.mapInfoReceiver = mapInfoReceiver;
        this.serverGreeter = serverGreeter;
    }

    public NetworkMessageListener createNetworkMessageListener() {
        return new NetworkMsgListenerContext(mapInfoReceiver);
    }

    public GameEventProducer createEventProducer(OutputFileWriter outputFileWriter) {
        return new BlockingGameEventProducer(
                outputFileWriter,
                new ProducerContext(serverGreeter)
        );
    }

}
