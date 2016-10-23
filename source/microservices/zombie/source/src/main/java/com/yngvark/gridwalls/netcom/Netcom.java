package com.yngvark.gridwalls.netcom;

public class Netcom<T extends ConnectionWrapper> {
    private final RetryConnecter<T> retryConnecter;
    private final RpcCaller<T> rpcCaller;

    public Netcom(RetryConnecter<T> retryConnecter, RpcCaller<T> rpcCaller) {
        this.retryConnecter = retryConnecter;
        this.rpcCaller = rpcCaller;
    }

    public RpcResult rpcCall(String rpcQueueName, String message) {
        ConnectStatus<T> connectStatus = retryConnecter.tryToEnsureConnected();
        if (connectStatus.succeeded())
            return rpcCaller.rpcCall(connectStatus.getConnectionWrapper(), rpcQueueName, message);
        else
            return new RpcFailed("Could not connect. Details: " + connectStatus.getConnectFailedDetails());
    }

    public void publish(String queueName, String message) {
        ConnectStatus connectStatus = retryConnecter.tryToEnsureConnected();
        if (connectStatus.succeeded()) {

        } else {

        }
        throw new RuntimeException("Not implemented");
    }

    public void disconnectIfConnected() {
        retryConnecter.disconnectIfConnected();
    }
}
