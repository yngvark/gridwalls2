package com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

class RabbitConsumer {
    private final Logger logger = getLogger(getClass());

    public RabbitConsumerData startConsume(
            RabbitConnection rabbitConnection, String exchange, RabbitMessageListener rabbitMessageListener) {

        boolean exchangeDurable = false;
        boolean exchangeAutoDelete = true;
        Map<String, Object> standardArgs = null;

        Channel eventsFromServerChannel;
        try {
            eventsFromServerChannel = rabbitConnection.getConnection().createChannel();
        } catch (IOException e) {
            throw new RuntimeException("Could not start consuming messages, because channel creation failure. Details: " + e.getMessage());
        }

        try {
            eventsFromServerChannel.exchangeDeclare(exchange, "fanout", exchangeDurable, exchangeAutoDelete, standardArgs);
        } catch (IOException e) {
            throw new RuntimeException("Could not start consuming messages, because channel declaration failure. Details: " + e.getMessage());
        }


        try {
            boolean queueDurable = false;
            boolean queueExclusive = false;
            boolean queueAutoDelete = true;
            eventsFromServerChannel.queueDeclare(exchange, queueDurable, queueExclusive, queueAutoDelete, standardArgs);
        } catch (IOException e) {
            throw new RuntimeException("Could not start consuming messages, because queue declaration failure. Details: " + e.getMessage());
        }

        try {
            eventsFromServerChannel.queueBind(exchange, exchange, "");
        } catch (IOException e) {
            throw new RuntimeException("Could not start consuming messages, because queue bind failure. Details: " + e.getMessage());
        }

        Consumer consumer = new DefaultConsumer(eventsFromServerChannel) {
            @Override
            public void handleDelivery(
                    String consumerTag,
                    Envelope envelope,
                    AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                rabbitMessageListener.messageReceived(message);
            }

            @Override
            public void handleCancel(String consumerTag) throws IOException {
                logger.error("handleCancel! ConsumerTag: " + consumerTag);
            }
        };

        String consumerTag;
        try {
            logger.info("Starting consume from queue: " + exchange);
            consumerTag = eventsFromServerChannel.basicConsume(exchange, true, consumer);
        } catch (IOException e) {
            throw new RuntimeException("Could not start consuming messages, because consume failure. Details: " + e.getMessage());
        }

        return new RabbitConsumerData(eventsFromServerChannel, consumerTag);
    }
}
