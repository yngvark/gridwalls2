package com.yngvark.gridwalls.netcom.rabbitmq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.yngvark.gridwalls.microservices.zombie.infrastructure.StackTracePrinter;
import com.yngvark.gridwalls.netcom.ConnectStatus;
import com.yngvark.gridwalls.netcom.Disconnected;
import com.yngvark.gridwalls.netcom.Connected;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitBrokerConnecter implements BrokerConnecter<RabbitConnectionWrapper> {
    private final StackTracePrinter stackTracePrinter;

    public RabbitBrokerConnecter(StackTracePrinter stackTracePrinter) {
        this.stackTracePrinter = stackTracePrinter;
    }

    @Override
    public ConnectStatus<RabbitConnectionWrapper> connect(String host, int timeoutMilliseconds) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setConnectionTimeout(timeoutMilliseconds);
        factory.setHost(host);

        try {
            Connection connection = factory.newConnection();
            return new Connected<>(new RabbitConnectionWrapper(stackTracePrinter, connection));
        } catch (IOException | TimeoutException e) {
            return new Disconnected<>(ExceptionUtils.getStackTrace(e));
        }
    }
}
