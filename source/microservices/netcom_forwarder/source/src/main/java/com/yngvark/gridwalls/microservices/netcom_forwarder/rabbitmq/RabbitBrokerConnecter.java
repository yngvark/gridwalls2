package com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq;

import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class RabbitBrokerConnecter {
    public RabbitConnection connect(String hostname) {
        Map<String, Object> clientProperties = new HashMap<>();
        clientProperties.put("consumer_cancel_notify", true);

        ConnectionFactory factory = new ConnectionFactory();
        factory.setConnectionTimeout(0);
        factory.setClientProperties(clientProperties);
        factory.setHost(hostname);
        factory.setRequestedHeartbeat(20);
        factory.setAutomaticRecoveryEnabled(true);

        try {
            return new RabbitConnection(factory.newConnection());
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException("Could not connect to host: " + hostname);
        }

    }
}

