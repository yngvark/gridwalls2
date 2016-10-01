package com.yngvark.gridwalls.microservices.zombie.netcom;

public class ConnectSucceeded implements ConnectAttempt {
    private ConnectionWrapper connection;

    public ConnectSucceeded(ConnectionWrapper connection) {
        this.connection = connection;
    }

    @Override
    public String getConnectFailedDetails() {
        return "";
    }

    @Override
    public RpcResult rpcCall(String rpcQueueName, String message) {
        return null;
    }
}
