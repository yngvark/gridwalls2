package com.yngvark.gridwalls.rabbitmq;

public interface RabbitMessageListener {
    void messageReceived(String msg);
}
