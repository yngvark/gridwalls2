package com.yngvark.netcom;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMqInitializer implements Initializer {
    private boolean connected = false;
    private Connection connection;

    @Override
    public void connect(String host) throws IOException, TimeoutException {
        if (host.isEmpty())
            throw new NoHostProvidedException("You must specify a hostname or IP.");

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("rabbithost");

        connection = new RabbitMqConnection(factory.newConnection());

        connected = true;
    }

    public Connection getConnection() {
        if (!connected)
            throw new NotYetConnectedException("Connect to a host before calling this method.");

        return connection;
    }
}
