package com.yngvark.netcom;

import com.rabbitmq.client.Channel;

public class RabbitMqChannel implements Topic {
    public RabbitMqChannel(Channel channel) {

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
