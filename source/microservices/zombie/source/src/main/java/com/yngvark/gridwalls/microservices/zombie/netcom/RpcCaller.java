package com.yngvark.gridwalls.microservices.zombie.netcom;

public interface RpcCaller {
    RpcResult rpcCall(ConnectionWrapper ConnectionWrapper, String rpcQueueName, String message);
}
