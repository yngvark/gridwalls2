package com.yngvark.gridwalls.microservices.zombie.netcom;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMqConnector implements BrokerConnecter {
    private boolean isConnected = false;

    @Override
    public ConnectResult connect(String host, int timeoutMilliseconds) {
        if (isConnected)
            throw new RuntimeException("Already connected.");

        return tryToConnect(timeoutMilliseconds);
    }

    private ConnectResult tryToConnect(int timeoutMilliseconds) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setConnectionTimeout(timeoutMilliseconds);
        factory.setHost("rabbithost");

        try {
            Connection connection = factory.newConnection();
            isConnected = true;
            return new ConnectSucceeded(connection);
        } catch (IOException | TimeoutException e) {
            return new ConnectFailed(ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    public ConnectResult connect(String host, int port, int timeoutMilliseconds) {
        return connect(host, timeoutMilliseconds);
    }
}
