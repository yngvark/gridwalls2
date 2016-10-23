package com.yngvark.gridwalls.netcom;

public class Connected implements ConnectStatus {
    private ConnectionWrapper connectionWrapper;

    public Connected(ConnectionWrapper connectionWrapper) {
        this.connectionWrapper = connectionWrapper;
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
    public ConnectionWrapper getConnectionWrapper() {
        return connectionWrapper;
    }
}
