package com.yngvark.gridwalls.rabbitmq;

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
    private final String host;
    private final ConnectionFactory connectionFactory;
    private final Sleeper sleeper;

    public RabbitBrokerConnecter(String host) {
        this.host = host;
        connectionFactory = new ConnectionFactory();
        sleeper = initSleeper();
    }

    private Sleeper initSleeper() {
        return (millis) -> {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
    }

    RabbitBrokerConnecter(String host, ConnectionFactory connectionFactory, Sleeper sleeper) {
        this.host = host;
        this.connectionFactory = connectionFactory;
        this.sleeper = sleeper;
    }

    public RabbitConnection connect() {
        while (true) {
            Map<String, Object> clientProperties = new HashMap<>();
            clientProperties.put("consumer_cancel_notify", true);

            connectionFactory.setConnectionTimeout(0);
            connectionFactory.setClientProperties(clientProperties);
            connectionFactory.setHost(host);
            connectionFactory.setRequestedHeartbeat(20);
            connectionFactory.setAutomaticRecoveryEnabled(true);

            if (System.getenv("RABBITMQ_USERNAME") != null) {
                logger.info("Found env variable for user.");
                connectionFactory.setUsername(System.getenv("RABBITMQ_USERNAME"));
            }
            if (System.getenv("RABBITMQ_PASSWORD") != null) {
                logger.info("Found env variable for password.");
                connectionFactory.setPassword(System.getenv("RABBITMQ_PASSWORD"));
            }

            logger.info("Connecting to " + host);
            try {
                Connection connection = connectionFactory.newConnection();
                logger.info("Connected!");
                return new RabbitConnection(connection);
            } catch (IOException | TimeoutException e) {
                logError(e);
                sleeper.sleep(3000l);
            }
        }
    }

    private void logError(Exception e) {
        String causeReason = e.getCause() == null ? "" : e.getCause().getMessage();
        String reason = e.getMessage() + " - " + causeReason;
        logger.info("Could not connect. Retrying soon. Exception: {}. Cause: {}", e.getClass().getSimpleName(), reason);
    }
}

