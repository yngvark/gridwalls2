package com.yngvark.gridwalls.microservices.zombie.netcom.rabbitmq;

import com.rabbitmq.client.Connection;
import com.yngvark.gridwalls.microservices.zombie.netcom.ConnectionWrapper;

class RabbitConnectionWrapper implements ConnectionWrapper<Connection> {
    private Connection connection;

    public RabbitConnectionWrapper(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean isConnected() {
        return connection.isOpen();
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

}
