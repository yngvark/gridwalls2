package com.yngvark.gridwalls.microservices.zombie.game.netcom.rabbitmq;

import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.Connection;
import com.yngvark.gridwalls.microservices.zombie.game.utils.StackTracePrinter;
import com.yngvark.gridwalls.netcom.connection.ConnectionWrapper;

import java.io.IOException;

public class RabbitConnectionWrapper implements ConnectionWrapper {
    private final StackTracePrinter stackTracePrinter;

    private Connection connection;

    public RabbitConnectionWrapper(StackTracePrinter stackTracePrinter, Connection connection) {
        this.stackTracePrinter = stackTracePrinter;
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

    @Override
    public void disconnectIfConnected() {
        try {
            connection.close();
        } catch (IOException e) {
            stackTracePrinter.print("Could not disconnect.", e);
        } catch (AlreadyClosedException e) {
            System.out.println("Connection already closed. Details: " + e.getMessage());
        }
    }

}
