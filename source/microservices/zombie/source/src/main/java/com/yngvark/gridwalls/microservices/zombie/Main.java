package com.yngvark.gridwalls.microservices.zombie;

import com.yngvark.gridwalls.core.CoordinateSerializer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Main {
    public static void main(String[] argv) throws IOException, TimeoutException, InterruptedException {
/*        // Set up depdendencies
        ZombieFactory zombieFactory = new ZombieFactory();
        ZombieMovedSerializer zombieMovedSerializer = new ZombieMovedSerializer(new CoordinateSerializer());
        Publisher publisher = new Publisher(zombieMovedSerializer);

        GameErrorHandler gameErrorHandler = new GameErrorHandler();
        ZombieRunnableFactory zombieRunnableFactory = new ZombieRunnableFactory(gameErrorHandler, publisher);

        RabbitMqConnector rabbitMqConnector = new RabbitMqConnector();

        GameRunner gameRunner = new GameRunner(rabbitMqConnector, zombieRunnableFactory, zombieFactory, gameErrorHandler);

        ExitSignalAwareRunner exitSignalAwareRunner = new ExitSignalAwareRunner();

        // Run
        exitSignalAwareRunner.run(gameRunner);*/
    }

}
