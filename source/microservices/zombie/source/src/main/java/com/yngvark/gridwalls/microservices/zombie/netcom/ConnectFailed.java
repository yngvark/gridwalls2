package com.yngvark.gridwalls.microservices.zombie.netcom;

public class ConnectFailed implements ConnectAttempt {
    private String reason;

    public ConnectFailed(String reason) {
        this.reason = reason;
    }

    @Override
    public boolean succeeded() {
        return false;
    }

    @Override
    public String getConnectFailedDetails() {
        return reason;
    }

    @Override
    public ConnectionWrapper getConnection() {
        throw new RuntimeException("Cannot get the connection of a failed connection attempt.");
    }
}
