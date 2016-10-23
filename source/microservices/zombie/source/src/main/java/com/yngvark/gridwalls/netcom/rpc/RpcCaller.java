package com.yngvark.gridwalls.netcom.rpc;

import com.yngvark.gridwalls.netcom.connection.ConnectionWrapper;

public interface RpcCaller<T extends ConnectionWrapper> {
    RpcResult rpcCall(T connectionWrapper, String rpcQueueName, String message);
}
