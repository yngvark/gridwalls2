package com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.Map;

public class RabbitConsumer {
    private final RabbitConnection rabbitConnection;
    private final MessageHandler messageHandler;

    public RabbitConsumer(RabbitConnection rabbitConnection,
            MessageHandler messageHandler) {
        this.rabbitConnection = rabbitConnection;
        this.messageHandler = messageHandler;
    }

    public void consume(String queueName) {
        String exchange = "ServerMessages";

        boolean exchangeDurable = false;
        boolean exchangeAutoDelete = true;
        Map<String, Object> standardArgs = null;

        Channel eventsFromServerChannel = null;
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
            eventsFromServerChannel.queueDeclare(queueName, queueDurable, queueExclusive, queueAutoDelete, standardArgs);
        } catch (IOException e) {
            throw new RuntimeException("Could not start consuming messages, because queue declaration failure. Details: " + e.getMessage());
        }

        try {
            eventsFromServerChannel.queueBind(queueName, exchange, "");
        } catch (IOException e) {
            throw new RuntimeException("Could not start consuming messages, because queue bind failure. Details: " + e.getMessage());
        }

        com.rabbitmq.client.Consumer consumer = new DefaultConsumer(eventsFromServerChannel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                    AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                messageHandler.messageReceived(message);
            }
        };

        try {
            eventsFromServerChannel.basicConsume(queueName, true, consumer);
        } catch (IOException e) {
            throw new RuntimeException("Could not start consuming messages, because consume failure. Details: " + e.getMessage());
        }

        System.out.println("Consuming events from queue: " + queueName);
    }
}
