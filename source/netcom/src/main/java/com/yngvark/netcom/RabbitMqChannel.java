package com.yngvark.netcom;

import com.rabbitmq.client.Channel;

public class RabbitMqChannel implements Topic {
    private Channel channel;

    public RabbitMqChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public String consume() {
        return null;
        channel.basicGet()
        channel.basicConsume(channel.get)
    }

    @Override
    public boolean hasMoreMessages() {
        return false;
    }
}
