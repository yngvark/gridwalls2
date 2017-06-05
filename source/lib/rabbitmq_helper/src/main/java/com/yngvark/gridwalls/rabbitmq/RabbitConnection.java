package com.yngvark.gridwalls.rabbitmq;

import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

public class RabbitConnection {
    private final Logger logger = getLogger(getClass());
    private final Map<String, Channel> exchangeChannnels = new HashMap<>();
    private final Connection connection;
    private boolean disconnected = false;

    public RabbitConnection(Connection connection) {
        this.connection = connection;
    }

    public Channel getChannelForExchange(String exchange) throws IOException {
        if (exchangeChannnels.get(exchange) == null) {
            Channel channel = connection.createChannel();
            exchangeChannnels.put(exchange, channel);
            channel.exchangeDeclare(exchange, "fanout", false, true, null);
        }

        return exchangeChannnels.get(exchange);
    }

    public synchronized void disconnectIfConnected() {
        logger.info("Disconnecting.");
        if (disconnected) {
            logger.info("Already disconnected.");
            return;
        }
        disconnected = true;

        try {
            connection.close();
        } catch (IOException e) {
            logger.warn("Could not disconnect.", e);
        } catch (AlreadyClosedException e) {
            logger.warn("Connection already closed. Details: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
