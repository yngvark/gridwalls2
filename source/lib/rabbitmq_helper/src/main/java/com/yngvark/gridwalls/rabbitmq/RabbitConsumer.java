package com.yngvark.gridwalls.rabbitmq;

import com.rabbitmq.client.Channel;

import java.io.IOException;

public class RabbitConsumer {
    private final String consumerName;
    private final String queue;
    private final Channel channel;
    private final String consumerTag;

    public RabbitConsumer(String consumerName, String queue, Channel channel, String consumerTag) {
        this.consumerName = consumerName;
        this.queue = queue;
        this.channel = channel;
        this.consumerTag = consumerTag;
    }

    public void stop() throws IOException {
        channel.basicCancel(consumerTag);
    }

    @Override
    public String toString() {
        return "RabbitConsumer{" +
                "consumerName='" + consumerName + '\'' +
                ", queue='" + queue + '\'' +
                ", consumerTag='" + consumerTag + '\'' +
                '}';
    }
}
