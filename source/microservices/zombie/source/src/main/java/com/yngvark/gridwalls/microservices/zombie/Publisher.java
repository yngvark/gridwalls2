package com.yngvark.gridwalls.microservices.zombie;

import com.rabbitmq.client.Channel;
import com.yngvark.gridwalls.core.CoordinateSerializer;

import java.io.IOException;

class Publisher {
    private ZombieMovedSerializer zombieMovedSerializer = new ZombieMovedSerializer(new CoordinateSerializer());

    public Publisher(ZombieMovedSerializer zombieMovedSerializer) {
        this.zombieMovedSerializer = zombieMovedSerializer;
    }

    public void publishEvent(ZombieMoved event, Channel channel) throws IOException {
        String message = zombieMovedSerializer.serialize(event);
        System.out.println("Sending message: " + message);

        channel.basicPublish("ZombieMoved", "", null, message.getBytes());
    }
}
