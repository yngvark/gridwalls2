package com.yngvark.gridwalls.microservices.zombie;

import com.rabbitmq.client.Channel;

import java.io.IOException;

public class Publisher {
    private ZombieMovedSerializer zombieMovedSerializer = new ZombieMovedSerializer(new CoordinateSerializer());
    private Channel channel;

    public Publisher(ZombieMovedSerializer zombieMovedSerializer, Channel channel) {
        this.zombieMovedSerializer = zombieMovedSerializer;
        this.channel = channel;
    }

    public void publishEvent(ZombieMoved event) throws IOException {
        String message = zombieMovedSerializer.serialize(event);
        System.out.println("Sending message: " + message);
        channel.basicPublish("ZombieMoved", "", null, message.getBytes());
    }
}
