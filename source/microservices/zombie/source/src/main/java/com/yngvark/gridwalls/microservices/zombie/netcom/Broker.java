package com.yngvark.gridwalls.microservices.zombie.netcom;

public interface Broker {
    RpcResult rpcCall(String rpcQueueName, String message) throws RpcException;
}
