package com.yngvark.gridwalls.microservices.zombie.netcom.rabbitmq;

import com.rabbitmq.client.Connection;
import com.yngvark.gridwalls.microservices.zombie.netcom.Broker;
import com.yngvark.gridwalls.microservices.zombie.netcom.ConnectAttempt;
import com.yngvark.gridwalls.microservices.zombie.netcom.Netcom;
import com.yngvark.gridwalls.microservices.zombie.netcom.RpcException;
import com.yngvark.gridwalls.microservices.zombie.netcom.RpcFailed;
import com.yngvark.gridwalls.microservices.zombie.netcom.RpcResult;

public class RabbitNetcom implements Broker {
    private final Netcom netcom;
    private final RabbitRpcCaller rpcCaller;

    private Connection connection;

    public RabbitNetcom(Netcom netcom, RabbitRpcCaller rpcCaller) {
        this.netcom = netcom;
        this.rpcCaller = rpcCaller;
    }

    @Override
    public RpcResult rpcCall(String rpcQueueName, String message) throws RpcException { // TODO Find out of exception, called method says nothing
        ConnectAttempt<Connection> connectAttempt = netcom.tryToEnsureConnected(new RabbitConnectionWrapper(this.connection));
        return connectAttempt.rpcCall(rpcQueueName, message);
    }

}
