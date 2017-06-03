package com.yngvark.gridwalls.microservices.netcom_forwarder.app.forward_msgs_to_network;

import com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq.RabbitConnection;
import com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq.RabbitPublisher;

public class RabbitPublisherFactory {

    public RabbitPublisher create(RabbitConnection rabbitConnection) {
        return new RabbitPublisher(rabbitConnection);
    }
}
