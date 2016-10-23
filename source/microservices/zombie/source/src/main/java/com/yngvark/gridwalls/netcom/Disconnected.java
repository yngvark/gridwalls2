package com.yngvark.gridwalls.netcom;

public class Disconnected implements ConnectStatus {
    private final String connectFailedDetails;

    public Disconnected(String connectFailedDetails) {
        this.connectFailedDetails = connectFailedDetails;
    }

    @Override
    public boolean succeeded() {
        return false;
    }

    @Override
    public boolean failed() {
        return true;
    }

    @Override
    public String getConnectFailedDetails() {
        return connectFailedDetails;
    }

    @Override
    public ConnectionWrapper getConnectionWrapper() {
        throw new RuntimeException("Cannot get the connection of a failed connection attempt.");
    }
}
