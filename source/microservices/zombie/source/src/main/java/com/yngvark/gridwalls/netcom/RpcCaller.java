package com.yngvark.gridwalls.netcom;

public interface RpcCaller<T extends ConnectionWrapper> {
    RpcResult rpcCall(T connectionWrapper, String rpcQueueName, String message);
}
