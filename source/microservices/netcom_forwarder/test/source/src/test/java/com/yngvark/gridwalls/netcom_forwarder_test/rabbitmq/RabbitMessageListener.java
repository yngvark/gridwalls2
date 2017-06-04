package com.yngvark.gridwalls.netcom_forwarder_test.rabbitmq;

public interface RabbitMessageListener {
    void messageReceived(String msg);
}
