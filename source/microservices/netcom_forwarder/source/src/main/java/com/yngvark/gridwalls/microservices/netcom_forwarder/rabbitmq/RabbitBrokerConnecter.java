package com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static org.slf4j.LoggerFactory.getLogger;

public class RabbitBrokerConnecter {
    private final Logger logger = getLogger(getClass());

    public RabbitConnection connect(String hostname) {

        while (true) {
            Map<String, Object> clientProperties = new HashMap<>();
            clientProperties.put("consumer_cancel_notify", true);

            ConnectionFactory factory = new ConnectionFactory();
            factory.setConnectionTimeout(0);
            factory.setClientProperties(clientProperties);
            factory.setHost(hostname);
            factory.setRequestedHeartbeat(20);
            factory.setAutomaticRecoveryEnabled(true);

            logger.info("Connecting to " + hostname);
            try {
                Connection connection = factory.newConnection();
                logger.info("Connected!");
                return new RabbitConnection(connection);
            } catch (ConnectException e) {
                String causeReason = e.getCause() == null ? "" : e.getCause().getMessage();
                String reason = e.getMessage() + " - " + causeReason;
                logger.error("Could not connect. Retrying soon. Reason: {}", reason);
                sleep();
            } catch (IOException | TimeoutException e) {
                throw new RuntimeException("Could not connect to host: " + hostname, e);
            }
        }
    }

    private void sleep() {
        try {
            Thread.sleep(3000l);
        } catch (InterruptedException ie) {
            throw new RuntimeException(ie);
        }
    }
}

