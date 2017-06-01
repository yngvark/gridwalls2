package com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;

import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

public class RabbitPublisher {
    private final Logger logger = getLogger(getClass());
    private final RabbitConnection connection;

    public RabbitPublisher(RabbitConnection connection) {
        this.connection = connection;
    }

    public void publish(String queue, String message) {
        Channel channel;

        try {
            channel = connection.getChannelForQueue(queue);

            try {
                channel.basicPublish(queue, "", null, message.getBytes());
            } catch (IOException e) {
                logger.error("Could not publish message, because publish failure. Details: " + e.getMessage());
            }
        } catch (IOException e) {
            logger.error("Could not publish message, because channel initialization failure. Details: " + e.getMessage());
        }
    }
}
