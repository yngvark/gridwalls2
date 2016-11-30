package com.yngvark.gridwalls.microservices.zombie.game.netcom.rabbitmq;

import com.rabbitmq.client.Channel;
import com.yngvark.gridwalls.microservices.zombie.game.utils.SafeMessageFormatter;
import com.yngvark.gridwalls.netcom.publish.NetcomFailed;
import com.yngvark.gridwalls.netcom.publish.NetcomResult;
import com.yngvark.gridwalls.netcom.publish.NetcomSucceeded;
import com.yngvark.gridwalls.netcom.publish.Publisher;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class RabbitPublisher implements Publisher<RabbitConnectionWrapper> {
    @Override
    public NetcomResult publish(RabbitConnectionWrapper connectionWrapper, String queue, String message) {
        Channel channel;

        try {
            channel = connectionWrapper.getChannelForQueue(queue);
        } catch (IOException e) {
            return new NetcomFailed("Could not publish message, because channel initialization failure. Details: " + e.getMessage());
        }

        try {
            String time = LocalTime.now().format(DateTimeFormatter.ofPattern("kk:mm:ss.SSS"));
            System.out.println(SafeMessageFormatter.format("[{0}] Sending message: {1}", time, message));
            channel.basicPublish(queue, "", null, message.getBytes());
        } catch (IOException e) {
            return new NetcomFailed("Could not publish message, because publish failure. Details: " + e.getMessage());
        }

        return new NetcomSucceeded();
    }
}
