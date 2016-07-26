package com.yngvark.netcom;

import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RabbitMqConnection implements Connection {
    private com.rabbitmq.client.Connection rabbitMqConnection;
    private Channel channel;
    private Map<String, RabbitMqChannel> topics = new HashMap<>();

    public RabbitMqConnection(com.rabbitmq.client.Connection rabbitMqConnection) throws IOException {
        this.rabbitMqConnection = rabbitMqConnection;
        channel = rabbitMqConnection.createChannel();
    }

    @Override
    public void subscribeTo(String topicName) throws IOException {
        channel.queueDeclare(topicName, false, true, true, null);
        topics.put(topicName, new RabbitMqChannel(channel));
    }

    @Override
    public Topic getSubscription(String topicName) {
        if (topics.get(topicName) == null)
            throw new NoSuchTopicException();

        return topics.get(topicName);
    }

    @Override
    public void publish(String topicName, String message) {
    }

    @Override
    public void disconnect() throws IOException {
        rabbitMqConnection.close();
    }
}
