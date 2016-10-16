package com.yngvark.gridwalls.netcom;

public class Netcom {
    private final RetryConnecter retryConnecter;
    private final RpcCaller rpcCaller;

    public Netcom(RetryConnecter retryConnecter, RpcCaller rpcCaller) {
        this.retryConnecter = retryConnecter;
        this.rpcCaller = rpcCaller;
    }

    public RpcResult rpcCall(String rpcQueueName, String message) {
        ConnectAttempt connectAttempt = retryConnecter.tryToEnsureConnected();
        if (connectAttempt.succeeded())
            return rpcCaller.rpcCall(connectAttempt.getConnectionWrapper(), rpcQueueName, message);
        else
            return new RpcFailed("Could not connect. Details: " + connectAttempt.getConnectFailedDetails());
    }

    public void publish(String queueName, String message) {
        ConnectAttempt connectAttempt = retryConnecter.tryToEnsureConnected();
        if (connectAttempt.succeeded()) {

        } else {

        }
        throw new RuntimeException("Not implemented");
    }

    public void disconnectIfConnected() {
        retryConnecter.disconnectIfConnected();
    }
}
