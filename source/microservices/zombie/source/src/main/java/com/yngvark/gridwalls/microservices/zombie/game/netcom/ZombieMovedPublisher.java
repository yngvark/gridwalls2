package com.yngvark.gridwalls.microservices.zombie.game.netcom;

import com.yngvark.gridwalls.microservices.zombie.game.ZombieMoved;
import com.yngvark.gridwalls.microservices.zombie.game.ZombieMovedSerializer;
import com.yngvark.gridwalls.netcom.Netcom;

public class ZombieMovedPublisher {
    private final ZombieMovedSerializer zombieMovedSerializer;
    private final Netcom netcom;

    public ZombieMovedPublisher(ZombieMovedSerializer zombieMovedSerializer, Netcom netcom) {
        this.zombieMovedSerializer = zombieMovedSerializer;
        this.netcom = netcom;
    }

    public void publishEvent(ZombieMoved event) {
        String zombieMovedEvent = zombieMovedSerializer.serialize(event);
        netcom.publish("ZombieMoved", zombieMovedEvent);
    }
}
