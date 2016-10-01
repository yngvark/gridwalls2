package com.yngvark.gridwalls.microservices.zombie.netcom;

public class ConnectSucceeded implements ConnectAttempt {
    private ConnectionWrapper connection;

    public ConnectSucceeded(ConnectionWrapper connection) {
        this.connection = connection;
    }

    @Override
    public boolean succeeded() {
        return true;
    }

    @Override
    public boolean failed() {
        return false;
    }

    @Override
    public String getConnectFailedDetails() {
        return "";
    }

    @Override
    public ConnectionWrapper getConnection() {
        return connection;
    }
}
