package com.yngvark.gridwalls.rabbitmq;

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

    public void publish(String exchange, String message) {
        Channel channel;

        try {
            channel = connection.getChannelForExchange(exchange);

            logger.info("Publish to exchange {}: {}", exchange, message);
            try {
                channel.basicPublish(exchange, "", null, message.getBytes());
            } catch (IOException e) {
                logger.error("Could not publish message, because publish failure. Details: " + e.getMessage());
            }
        } catch (IOException e) {
            logger.error("Could not publish message, because channel initialization failure. Details: " + e.getMessage());
        }
    }
}
