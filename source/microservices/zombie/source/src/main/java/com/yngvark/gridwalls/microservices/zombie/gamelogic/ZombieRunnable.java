package com.yngvark.gridwalls.microservices.zombie.gamelogic;

import com.rabbitmq.client.Channel;
import com.yngvark.gridwalls.microservices.zombie.infrastructure.GameErrorHandler;
import com.yngvark.gridwalls.microservices.zombie.netcom.Publisher;

public class ZombieRunnable implements Runnable {
    private final Zombie zombie;
    private final Publisher publisher;
    private final Channel channel;
    private final GameErrorHandler gameErrorHandler;

    public ZombieRunnable(Zombie zombie, Publisher publisher, Channel channel,
            GameErrorHandler gameErrorHandler) {
        this.zombie = zombie;
        this.publisher = publisher;
        this.channel = channel;
        this.gameErrorHandler = gameErrorHandler;
    }

    @Override
    public void run() {
        try {
            System.out.println("zombie1.nextTurn()");
            ZombieMoved event = zombie.nextTurn();
            publisher.publishEvent(event, channel);
        } catch (Throwable e) {
            gameErrorHandler.handle(e);
        }
    }


}
