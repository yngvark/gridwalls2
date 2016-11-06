package com.yngvark.gridwalls.microservices.zombie.game.netcom.rabbitmq;

import com.rabbitmq.client.Channel;
import com.yngvark.gridwalls.microservices.zombie.game.utils.SafeMessageFormatter;
import com.yngvark.gridwalls.netcom.publish.PublishFailed;
import com.yngvark.gridwalls.netcom.publish.PublishResult;
import com.yngvark.gridwalls.netcom.publish.PublishSucceeded;
import com.yngvark.gridwalls.netcom.publish.Publisher;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("H:mm:s.n"));
            System.out.println(SafeMessageFormatter.format("[{0}] Sending message: {1}", time, message));
            channel.basicPublish(queue, "", null, message.getBytes());
        } catch (IOException e) {
            return new PublishFailed("Could not publish message, because publish failure. Details: " + e.getMessage());
        }

        return new PublishSucceeded();
    }
}
