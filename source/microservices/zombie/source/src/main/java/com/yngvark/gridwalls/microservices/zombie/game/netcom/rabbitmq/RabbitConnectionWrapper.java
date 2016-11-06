package com.yngvark.gridwalls.microservices.zombie.game.netcom.rabbitmq;

import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.yngvark.gridwalls.microservices.zombie.game.utils.StackTracePrinter;
import com.yngvark.gridwalls.netcom.connection.ConnectionWrapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RabbitConnectionWrapper implements ConnectionWrapper {
    private final StackTracePrinter stackTracePrinter;
    private final Connection connection;

    private final Map<String, Channel> exchangeChannnels = new HashMap<>();

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

    public Channel getChannelForExchange(String queue) throws IOException {
        if (exchangeChannnels.get(queue) == null) {
            Channel channel = connection.createChannel();
            exchangeChannnels.put(queue, channel);
            channel.exchangeDeclare(queue, "fanout", false, true, null);
        }

        return exchangeChannnels.get(queue);
    }
}
