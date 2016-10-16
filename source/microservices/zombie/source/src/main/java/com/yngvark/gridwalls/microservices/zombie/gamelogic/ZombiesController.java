package com.yngvark.gridwalls.microservices.zombie.gamelogic;

import com.yngvark.gridwalls.microservices.zombie.infrastructure.GameErrorHandler;
import com.yngvark.gridwalls.netcom.Publisher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ZombiesController {
    private final ZombieFactory zombieFactory;
    private final Publisher publisher;

    private List<Zombie> zombies = new ArrayList<>();
    private boolean initialized;

    public ZombiesController(ZombieFactory zombieFactory, Publisher publisher) {
        this.zombieFactory = zombieFactory;
        this.publisher = publisher;
    }

    public void nextTurn() {
        if (!initialized) {
            createZombies();
            initialized = true;
        }

        for (Zombie zombie : zombies) {
            runNextTurnOn(zombie);
        }
    }

    private void createZombies() {
        zombies = zombieFactory.createZombies(10, 10);
    }

    private void runNextTurnOn(Zombie zombie) {
        System.out.println("zombie1.nextTurn()");
        ZombieMoved event = zombie.nextTurn();
        try {
            publisher.publishEvent(event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
