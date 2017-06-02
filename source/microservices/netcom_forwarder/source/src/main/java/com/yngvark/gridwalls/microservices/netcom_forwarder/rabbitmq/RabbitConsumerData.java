package com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

class RabbitConsumerData {
    private final Logger logger = getLogger(getClass());
    private final Channel channel;
    private final String consumerTag;

    public RabbitConsumerData(Channel channel, String consumerTag) {
        this.channel = channel;
        this.consumerTag = consumerTag;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getConsumerTag() {
        return consumerTag;
    }
}
