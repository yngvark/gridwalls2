package com.yngvark.gridwalls.microservices.zombie.netcom;

public interface ConnectAttempt<T> {
    RpcResult rpcCall(String rpcQueueName, String message);
}
