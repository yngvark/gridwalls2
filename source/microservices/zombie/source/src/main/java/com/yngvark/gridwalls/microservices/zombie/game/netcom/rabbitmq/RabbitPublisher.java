package com.yngvark.gridwalls.microservices.zombie.game.netcom.rabbitmq;

import com.rabbitmq.client.Channel;
import com.yngvark.gridwalls.netcom.publish.PublishFailed;
import com.yngvark.gridwalls.netcom.publish.PublishResult;
import com.yngvark.gridwalls.netcom.publish.PublishSucceeded;
import com.yngvark.gridwalls.netcom.publish.Publisher;

import java.io.IOException;

public class RabbitPublisher implements Publisher<RabbitConnectionWrapper> {
    @Override
    public PublishResult publish(RabbitConnectionWrapper connectionWrapper, String queue, String message) {
        Channel channel;

        try {
            channel = connectionWrapper.getChannelForExchange(queue);
        } catch (IOException e) {
            return new PublishFailed("Could not publish message, because channel initialization failure. Details: " + e.getMessage());
        }

        try {
            channel.basicPublish(queue, "", null, message.getBytes());
        } catch (IOException e) {
            return new PublishFailed("Could not publish message, because publish failure. Details: " + e.getMessage());
        }

        return new PublishSucceeded();
    }
}
