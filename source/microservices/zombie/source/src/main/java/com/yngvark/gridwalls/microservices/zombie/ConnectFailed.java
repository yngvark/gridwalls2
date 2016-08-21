package com.yngvark.gridwalls.microservices.zombie;

import com.rabbitmq.client.Connection;

public class ConnectFailed implements ConnectResult {
    private String reason;

    public ConnectFailed(String reason) {
        this.reason = reason;
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public String getConnectFailedDetails() {
        return reason;
    }

    @Override
    public Connection getConnection() {
        throw new RuntimeException("Cannot get the connection of a failed connection attempt.");
    }
}
