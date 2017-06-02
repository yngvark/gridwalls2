package com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq;

public interface RabbitMessageListener {
    void messageReceived(String msg);
}
