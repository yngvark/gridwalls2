package com.yngvark.gridwalls.microservices.zombie.game.netcom;

import com.yngvark.gridwalls.microservices.zombie.game.ZombieMoved;
import com.yngvark.gridwalls.microservices.zombie.game.ZombieMovedSerializer;
import com.yngvark.gridwalls.netcom.Netcom;

import java.io.IOException;

public class Publisher {
    private ZombieMovedSerializer zombieMovedSerializer;
    private Netcom netcom;

    public Publisher(ZombieMovedSerializer zombieMovedSerializer, Netcom netcom) {
        this.zombieMovedSerializer = zombieMovedSerializer;
        this.netcom = netcom;
    }

    public void publishEvent(ZombieMoved event) throws IOException {
        String zombieMovedEvent = zombieMovedSerializer.serialize(event);
        System.out.println("Sending message: " + zombieMovedEvent);

        netcom.publish("ZombieMoved", zombieMovedEvent);
    }
}
