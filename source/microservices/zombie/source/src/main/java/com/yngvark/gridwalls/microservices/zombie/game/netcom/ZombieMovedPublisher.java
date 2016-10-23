package com.yngvark.gridwalls.microservices.zombie.game.netcom;

import com.yngvark.gridwalls.microservices.zombie.game.ZombieMoved;
import com.yngvark.gridwalls.microservices.zombie.game.ZombieMovedSerializer;
import com.yngvark.gridwalls.netcom.Netcom;

public class ZombieMovedPublisher {
    private ZombieMovedSerializer zombieMovedSerializer;
    private Netcom netcom;

    public ZombieMovedPublisher(ZombieMovedSerializer zombieMovedSerializer, Netcom netcom) {
        this.zombieMovedSerializer = zombieMovedSerializer;
        this.netcom = netcom;
    }

    public void publishEvent(ZombieMoved event) {
        String zombieMovedEvent = zombieMovedSerializer.serialize(event);
        System.out.println("Sending message: " + zombieMovedEvent);

        netcom.publish("ZombieMoved", zombieMovedEvent);
    }
}
