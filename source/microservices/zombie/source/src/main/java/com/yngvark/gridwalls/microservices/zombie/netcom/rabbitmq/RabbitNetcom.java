package com.yngvark.gridwalls.microservices.zombie.netcom.rabbitmq;

import com.rabbitmq.client.Connection;
import com.yngvark.gridwalls.microservices.zombie.netcom.Broker;
import com.yngvark.gridwalls.microservices.zombie.netcom.ConnectAttempt;
import com.yngvark.gridwalls.microservices.zombie.netcom.RetryConnecter;
import com.yngvark.gridwalls.microservices.zombie.netcom.RpcException;
import com.yngvark.gridwalls.microservices.zombie.netcom.RpcResult;

public class RabbitNetcom implements Broker {
    private final RetryConnecter retryConnecter;
    private final RabbitRpcCaller rpcCaller;

    private Connection connection;

    public RabbitNetcom(RetryConnecter retryConnecter, RabbitRpcCaller rpcCaller) {
        this.retryConnecter = retryConnecter;
        this.rpcCaller = rpcCaller;
    }

    @Override
    public RpcResult rpcCall(String rpcQueueName, String message) throws RpcException { // TODO Find out of exception, called method says nothing
        ConnectAttempt<Connection> connectAttempt = retryConnecter.tryToEnsureConnected();
        return connectAttempt.rpcCall(rpcQueueName, message);
    }

}
