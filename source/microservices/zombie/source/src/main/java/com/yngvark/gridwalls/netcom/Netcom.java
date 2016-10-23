package com.yngvark.gridwalls.netcom;

import com.yngvark.gridwalls.netcom.connection.connect_status.ConnectStatus;
import com.yngvark.gridwalls.netcom.connection.ConnectionWrapper;
import com.yngvark.gridwalls.netcom.connection.RetryConnecter;
import com.yngvark.gridwalls.netcom.publish.PublishResult;
import com.yngvark.gridwalls.netcom.publish.Publisher;
import com.yngvark.gridwalls.netcom.rpc.RpcCaller;
import com.yngvark.gridwalls.netcom.rpc.RpcFailed;
import com.yngvark.gridwalls.netcom.rpc.RpcResult;

public class Netcom<T extends ConnectionWrapper> {
    private final RetryConnecter<T> retryConnecter;
    private final RpcCaller<T> rpcCaller;
    private final Publisher<T> publisher;

    public Netcom(RetryConnecter<T> retryConnecter, RpcCaller<T> rpcCaller, Publisher<T> publisher) {
        this.retryConnecter = retryConnecter;
        this.rpcCaller = rpcCaller;
        this.publisher = publisher;
    }

    public RpcResult rpcCall(String rpcQueueName, String message) {
        ConnectStatus<T> connectStatus = retryConnecter.tryToEnsureConnected();
        if (connectStatus.succeeded())
            return rpcCaller.rpcCall(connectStatus.getConnectionWrapper(), rpcQueueName, message);
        else
            return new RpcFailed("Could not connect. Details: " + connectStatus.getConnectFailedDetails());
    }

    public PublishResult publish(String queueName, String message) {
        ConnectStatus connectStatus = retryConnecter.tryToEnsureConnected();
        if (connectStatus.succeeded()) {
            return publisher.publish(queueName, message);
        } else {
            throw new RuntimeException("Not implemented");
        }
    }

    public void disconnectIfConnected() {
        retryConnecter.disconnectIfConnected();
    }
}
