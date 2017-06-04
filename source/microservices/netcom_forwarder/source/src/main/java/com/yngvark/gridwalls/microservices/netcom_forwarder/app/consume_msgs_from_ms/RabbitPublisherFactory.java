package com.yngvark.gridwalls.microservices.netcom_forwarder.app.consume_msgs_from_ms;

import com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq.RabbitConnection;
import com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq.RabbitPublisher;

class RabbitPublisherFactory {

    public RabbitPublisher create(RabbitConnection rabbitConnection) {
        return new RabbitPublisher(rabbitConnection);
    }
}
