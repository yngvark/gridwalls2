package com.yngvark.gridwalls.netcom;

public class ConnectSucceeded implements ConnectAttempt {
    private ConnectionWrapper connectionWrapper;

    public ConnectSucceeded(ConnectionWrapper connectionWrapper) {
        this.connectionWrapper = connectionWrapper;
    }

    @Override
    public boolean succeeded() {
        return false;
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
    public ConnectionWrapper getConnectionWrapper() {
        return connectionWrapper;
    }
}
