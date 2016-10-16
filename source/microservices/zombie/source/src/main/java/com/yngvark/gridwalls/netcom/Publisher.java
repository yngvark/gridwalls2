package com.yngvark.gridwalls.netcom;

import com.yngvark.gridwalls.microservices.zombie.gamelogic.ZombieMoved;
import com.yngvark.gridwalls.microservices.zombie.gamelogic.ZombieMovedSerializer;

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
