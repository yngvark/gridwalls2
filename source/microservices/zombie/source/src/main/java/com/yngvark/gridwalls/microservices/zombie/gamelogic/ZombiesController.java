package com.yngvark.gridwalls.microservices.zombie.gamelogic;

import com.rabbitmq.client.Channel;
import com.yngvark.gridwalls.microservices.zombie.infrastructure.GameErrorHandler;
import com.yngvark.gridwalls.microservices.zombie.netcom.Publisher;

import java.util.ArrayList;
import java.util.List;

public class ZombiesController {
    private final ZombieFactory zombieFactory;
    private final Publisher publisher;
    private final Channel channel;
    private final GameErrorHandler gameErrorHandler;

    private List<Zombie> zombies = new ArrayList<>();

    public ZombiesController(ZombieFactory zombieFactory, Publisher publisher, Channel channel,
            GameErrorHandler gameErrorHandler) {
        this.zombieFactory = zombieFactory;
        this.publisher = publisher;
        this.channel = channel;
        this.gameErrorHandler = gameErrorHandler;
    }

    public void nextTurn() {
        if (!initialized)
            init();
        for (Zombie zombie : zombies) {
            runNextTurnOn(zombie);
        }
    }

    private void init() {
        zombies = zombieFactory.createZombies(10, 10);
    }

    private void runNextTurnOn(Zombie zombie) {
        try {
            System.out.println("zombie1.nextTurn()");
            ZombieMoved event = zombie.nextTurn();
            publisher.publishEvent(event, channel);
        } catch (Throwable e) {
            gameErrorHandler.handle(e);
        }
    }
}
