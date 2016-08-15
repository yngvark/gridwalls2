package com.yngvark.gridwalls.microservices.zombie;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.yngvark.gridwalls.core.CoordinateSerializer;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

public class Main2 {
    public static void main(String[] argv) throws IOException, TimeoutException, InterruptedException {
        //Injector injector = Guice.createInjector(new ModuleConfig());
        //GameRunner2 gameRunner2 = injector.getInstance(GameRunner2.class);

        // Set up depdendencies
        ZombieFactory zombieFactory = new ZombieFactory();
        ZombieMovedSerializer zombieMovedSerializer = new ZombieMovedSerializer(new CoordinateSerializer());
        Publisher publisher = new Publisher(zombieMovedSerializer);

        GameErrorHandler gameErrorHandler = new GameErrorHandler();
        ZombieRunnableFactory zombieRunnableFactory = new ZombieRunnableFactory(gameErrorHandler, publisher);

        RabbitMqConnector rabbitMqConnector = new RabbitMqConnector();

        GameRunner2 gameRunner = new GameRunner2(rabbitMqConnector, zombieRunnableFactory, zombieFactory, gameErrorHandler);
        gameRunner.run();
    }
}
