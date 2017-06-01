package com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq;

public interface MessageHandler {
    void messageReceived(String msg);
}
