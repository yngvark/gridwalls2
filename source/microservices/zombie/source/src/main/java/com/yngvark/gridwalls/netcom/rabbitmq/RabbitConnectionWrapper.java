package com.yngvark.gridwalls.netcom.rabbitmq;

import com.rabbitmq.client.Connection;
import com.yngvark.gridwalls.microservices.zombie.infrastructure.StackTracePrinter;
import com.yngvark.gridwalls.netcom.ConnectionWrapper;

import java.io.IOException;

class RabbitConnectionWrapper implements ConnectionWrapper<Connection> {
    private final StackTracePrinter stackTracePrinter;

    private Connection connection;

    public RabbitConnectionWrapper(StackTracePrinter stackTracePrinter, Connection connection) {
        this.stackTracePrinter = stackTracePrinter;
        this.connection = connection;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void disconnectIfConnected() {
        try {
            connection.close();
        } catch (IOException e) {
            stackTracePrinter.print("Could not disconnect.", e);
        }
    }

}
