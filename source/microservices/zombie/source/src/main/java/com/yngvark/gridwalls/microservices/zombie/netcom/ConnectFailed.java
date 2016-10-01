package com.yngvark.gridwalls.microservices.zombie.netcom;

import com.yngvark.gridwalls.microservices.zombie.utils.MessageFormatter;

public class ConnectFailed implements ConnectAttempt {
    private final MessageFormatter messageFormatter;

    private String reason;

    public ConnectFailed(MessageFormatter messageFormatter, String reason) {
        this.messageFormatter = messageFormatter;
        this.reason = reason;
    }

    @Override
    public RpcResult rpcCall(String rpcQueueName, String message) {
        return new RpcFailed(messageFormatter.format("Not connected. Reason: {0}. Attempted to do RPC call to queue '{0}' with message: {1}",
                reason, rpcQueueName, message));
    }
}
