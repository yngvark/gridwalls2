package com.yngvark.gridwalls.microservices.zombie.netcom.rabbitmq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.yngvark.gridwalls.microservices.zombie.netcom.ConnectAttempt;
import com.yngvark.gridwalls.microservices.zombie.netcom.ConnectFailedFactory;
import com.yngvark.gridwalls.microservices.zombie.netcom.ConnectSucceeded;
import com.yngvark.gridwalls.microservices.zombie.netcom.OneTImeConnecter;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

class RabbitMqOneTImeConnecter implements OneTImeConnecter {
    private final ConnectFailedFactory connectFailedFactory;

    public RabbitMqOneTImeConnecter(ConnectFailedFactory connectFailedFactory) {
        this.connectFailedFactory = connectFailedFactory;
    }

    public ConnectAttempt connect(String host, int timeoutMilliseconds) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setConnectionTimeout(timeoutMilliseconds);
        factory.setHost(host);

        try {
            Connection connection = factory.newConnection();
            return new ConnectSucceeded(new RabbitConnectionWrapper(connection));
        } catch (IOException | TimeoutException e) {
            return connectFailedFactory.failed(ExceptionUtils.getStackTrace(e));
        }
    }

    public ConnectAttempt connect(String host, int timeoutMilliseconds, Connection connection) {
        if (connection.isOpen())
            return new ConnectSucceeded(new RabbitConnectionWrapper(connection));

        return connect(host, timeoutMilliseconds);
    }
}
