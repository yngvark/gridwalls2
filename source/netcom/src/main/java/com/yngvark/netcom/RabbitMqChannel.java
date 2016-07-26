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
    }

    @Override
    public boolean hasMoreMessages() {
        return false;
    }
}
