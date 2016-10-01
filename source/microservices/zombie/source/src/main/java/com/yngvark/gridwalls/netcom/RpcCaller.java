package com.yngvark.gridwalls.netcom;

public interface RpcCaller<T> {
    RpcResult rpcCall(ConnectionWrapper<T> connectionWrapper, String rpcQueueName, String message);
}
