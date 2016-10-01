package com.yngvark.gridwalls.microservices.zombie.netcom.rabbitmq;

import com.rabbitmq.client.Connection;
import com.yngvark.gridwalls.microservices.zombie.netcom.Broker;
import com.yngvark.gridwalls.microservices.zombie.netcom.ConnectAttempt;
import com.yngvark.gridwalls.microservices.zombie.netcom.RetryConnecter;
import com.yngvark.gridwalls.microservices.zombie.netcom.RpcException;
import com.yngvark.gridwalls.microservices.zombie.netcom.RpcFailed;
import com.yngvark.gridwalls.microservices.zombie.netcom.RpcResult;

public class RabbitNetcomOld implements Broker {
    private final RetryConnecter retryConnecter;
    private final RabbitMqOneTImeConnecter connector;
    private final RabbitRpcCaller rpcCaller;

    private Connection connection;

    public RabbitNetcomOld(RetryConnecter retryConnecter, RabbitMqOneTImeConnecter connector, RabbitRpcCaller rpcCaller) {
        this.retryConnecter = retryConnecter;
        this.connector = connector;
        this.rpcCaller = rpcCaller;
    }

    @Override
    public RpcResult rpcCall(String rpcQueueName, String message) throws RpcException {
        ConnectAttempt<Connection> connectAttempt = retryConnecter.tryToEnsureConnected(new RabbitConnectionWrapper(connection));
        if (connectAttempt.succeeded())
            connection = connectAttempt.getConnection().getConnection();
        else
            return new RpcFailed("Could not connect.");

        return doRpcCall(rpcQueueName, message);
    }

    private RpcResult doRpcCall(String rpcQueueName, String message) {
        return rpcCaller.rpcCall(connection, rpcQueueName, message);
    }

    public RpcResult rpcCall2(String rpcQueueName, String message) throws RpcException {
        ConnectAttempt<Connection> connectAttempt = retryConnecter.tryToEnsureConnected(new RabbitConnectionWrapper(this.connection));
        return connectAttempt.rpcCall(rpcQueueName, message);
    }
}
