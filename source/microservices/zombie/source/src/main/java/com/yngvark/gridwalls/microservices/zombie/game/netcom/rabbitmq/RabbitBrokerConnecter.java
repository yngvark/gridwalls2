package com.yngvark.gridwalls.microservices.zombie.game.netcom.rabbitmq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.yngvark.gridwalls.microservices.zombie.game.utils.StackTracePrinter;
import com.yngvark.gridwalls.netcom.connection.BrokerConnecter;
import com.yngvark.gridwalls.netcom.connection.connect_status.ConnectionStatus;
import com.yngvark.gridwalls.netcom.connection.connect_status.Connected;
import com.yngvark.gridwalls.netcom.connection.connect_status.Disconnected;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitBrokerConnecter implements BrokerConnecter<RabbitConnectionWrapper> {
    private final StackTracePrinter stackTracePrinter;

    public RabbitBrokerConnecter(StackTracePrinter stackTracePrinter) {
        this.stackTracePrinter = stackTracePrinter;
    }

    @Override
    public ConnectionStatus<RabbitConnectionWrapper> connect(String host, int timeoutMilliseconds) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setConnectionTimeout(timeoutMilliseconds);
        factory.setHost(host);
        factory.setRequestedHeartbeat(20);
        factory.setAutomaticRecoveryEnabled(true);

        try {
            Connection connection = factory.newConnection();
            return new Connected<>(new RabbitConnectionWrapper(stackTracePrinter, connection));
        } catch (IOException | TimeoutException e) {
            return new Disconnected<>(ExceptionUtils.getStackTrace(e));
        }
    }
}
