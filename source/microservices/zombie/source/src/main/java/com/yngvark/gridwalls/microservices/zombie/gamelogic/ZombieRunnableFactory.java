package com.yngvark.gridwalls.microservices.zombie.gamelogic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.yngvark.gridwalls.microservices.zombie.infrastructure.GameErrorHandler;
import com.yngvark.gridwalls.microservices.zombie.netcom.Publisher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class ZombieRunnableFactory {
    private final GameErrorHandler gameErrorHandler;
    private final Publisher publisher;

    public ZombieRunnableFactory(GameErrorHandler gameErrorHandler, Publisher publisher) {
        this.gameErrorHandler = gameErrorHandler;
        this.publisher = publisher;
    }

    public List<Runnable> createZombieRunnables(List<Zombie> zombies, Connection connection) throws IOException {
        List<Runnable> runnables = new ArrayList<>();
        for (Zombie zombie : zombies) {
            Channel channel = connection.createChannel();
            channel.exchangeDeclare("ZombieMoved", "fanout", true);

            runnables.add(new ZombieRunnable(zombie, publisher, channel, gameErrorHandler));
        }

        return runnables;
    }

}
