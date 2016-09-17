package com.yngvark.gridwalls.microservices.zombie.netcom;

import com.rabbitmq.client.Connection;

public class ConnectSucceeded implements ConnectResult {
    private Connection connection;

    public ConnectSucceeded(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public String getConnectFailedDetails() {
        return "";
    }

    @Override
    public Connection getConnection() {
        return connection;
    }
}
