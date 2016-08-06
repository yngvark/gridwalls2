package com.yngvark.gridwalls.microservices.zombie;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

class RabbitMqConnector {
    private Connection connection;
    private boolean isConnected = false;

    public Connection connect() throws IOException, TimeoutException {
        if (isConnected)
            throw new RuntimeException("Already connected");

        connection = createConnection();
        isConnected = true;

        return connection;
    }

    private Connection createConnection() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("rabbithost");

        return factory.newConnection();
    }

    public void disconnect() throws IOException, TimeoutException {
        connection.close();
    }
}
