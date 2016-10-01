package com.yngvark.gridwalls.microservices.zombie.netcom.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.RpcClient;
import com.yngvark.gridwalls.microservices.zombie.netcom.RpcResult;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

class RabbitRpcCaller {
    public RpcResult rpcCall(com.rabbitmq.client.Connection connection, String rpcQueueName, String message) {
        try {
            return tryToRpcCall(connection, rpcQueueName, message);
        } catch (IOException | TimeoutException e) {
            return RpcResult.failed("Could not do RPC call. Details: " + ExceptionUtils.getStackTrace(e));
        }
    }

    private RpcResult tryToRpcCall(com.rabbitmq.client.Connection connection, String rpcQueueName, String message)
            throws IOException, TimeoutException {
        Channel channel = connection.createChannel();
        channel.queueDeclare(rpcQueueName, true, false, true, null);

        RpcClient rpc = new RpcClient(channel, "", rpcQueueName);
        System.out.println("Receiving game config.");
        String gameConfigTxt = rpc.stringCall(message);
        System.out.println("Response: " + gameConfigTxt);

        rpc.close();
        channel.close();

        return RpcResult.succeeded(gameConfigTxt);
    }
}
