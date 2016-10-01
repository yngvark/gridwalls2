package com.yngvark.gridwalls.microservices.zombie.netcom.rabbitmq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.yngvark.gridwalls.microservices.zombie.netcom.ConnectAttempt;
import com.yngvark.gridwalls.microservices.zombie.netcom.ConnectFailed;
import com.yngvark.gridwalls.microservices.zombie.netcom.ConnectSucceeded;
import com.yngvark.gridwalls.microservices.zombie.netcom.Connector;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

class RabbitMqConnector implements Connector {
    public ConnectAttempt connect(String host, int timeoutMilliseconds) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setConnectionTimeout(timeoutMilliseconds);
        factory.setHost(host);

        try {
            Connection connection = factory.newConnection();
            return new ConnectSucceeded(new RabbitConnectionWrapper(connection));
        } catch (IOException | TimeoutException e) {
            return new ConnectFailed(ExceptionUtils.getStackTrace(e));
        }
    }
}
