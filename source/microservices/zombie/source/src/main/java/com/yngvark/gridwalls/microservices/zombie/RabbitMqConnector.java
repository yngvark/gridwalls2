package com.yngvark.gridwalls.microservices.zombie;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

class RabbitMqConnector implements MessageBusConnecter {
    private Connection connection;
    private boolean isConnected = false;

    public ConnectResult connect(int timeout) {
        if (isConnected)
            throw new RuntimeException("Already connected.");

        ConnectResult connectResult = tryToConnect(timeout);

        return connectResult;
    }

    private ConnectResult tryToConnect(int timeout) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setConnectionTimeout(timeout);
        factory.setHost("rabbithost");

        try {
            Connection connection = factory.newConnection();
            isConnected = true;
            return new ConnectSucceeded(connection);
        } catch (IOException | TimeoutException e) {
            return new ConnectFailed(ExceptionUtils.getStackTrace(e));
        }
    }

    public void disconnect() throws IOException, TimeoutException {
        if (isConnected)
            connection.close();
    }
}
