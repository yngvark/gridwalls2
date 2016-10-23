package com.yngvark.gridwalls.microservices.zombie.game;

import com.yngvark.gridwalls.core.MapDimensions;
import com.yngvark.gridwalls.microservices.zombie.game.netcom.ZombieMovedPublisher;

import java.util.ArrayList;
import java.util.List;

public class ZombiesController {
    private final ZombieFactory zombieFactory;
    private final ZombieMovedPublisher zombieMovedPublisher;

    private List<Zombie> zombies = new ArrayList<>();
    private boolean initialized;

    public ZombiesController(ZombieFactory zombieFactory, ZombieMovedPublisher zombieMovedPublisher) {
        this.zombieFactory = zombieFactory;
        this.zombieMovedPublisher = zombieMovedPublisher;
    }

    public void nextTurn() {
        if (!initialized) {
            createZombies();
            initialized = true;
        }

        zombies.forEach(this::runNextTurnOn);
    }

    private void createZombies() {
        zombies = zombieFactory.createZombies(new MapDimensions(10, 10));
    }

    private void runNextTurnOn(Zombie zombie) {
        System.out.println("zombie1.nextTurn()");
        ZombieMoved event = zombie.nextTurn();
        zombieMovedPublisher.publishEvent(event);
    }
}
