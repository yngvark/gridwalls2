package com.yngvark.gridwalls.netcom.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.RpcClient;
import com.yngvark.gridwalls.netcom.RpcCaller;
import com.yngvark.gridwalls.netcom.RpcFailed;
import com.yngvark.gridwalls.netcom.RpcResult;
import com.yngvark.gridwalls.netcom.RpcSucceeded;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitRpcCaller implements RpcCaller<RabbitConnectionWrapper> {
    @Override
    public RpcResult rpcCall(RabbitConnectionWrapper connectionWrapper, String rpcQueueName, String message) {
        Connection connection = connectionWrapper.getConnection();

        try {
            return tryToRpcCall(connection, rpcQueueName, message);
        } catch (IOException | TimeoutException e) {
            return new RpcFailed("Could not do RPC call. Details: " + ExceptionUtils.getStackTrace(e));
        }
    }

    private RpcResult tryToRpcCall(Connection connection, String rpcQueueName, String message)
            throws IOException, TimeoutException {
        Channel channel = connection.createChannel();

        declareQueue(rpcQueueName, channel);

        String exchange = "";
        RpcClient rpcClient = new RpcClient(channel, exchange, rpcQueueName);

        System.out.println("Receiving game config.");
        String gameConfigTxt = rpcClient.stringCall(message);
        System.out.println("Response: " + gameConfigTxt);

        rpcClient.close();
        channel.close();

        return new RpcSucceeded(gameConfigTxt);
    }

    private void declareQueue(String rpcQueueName, Channel channel) throws IOException {
        channel.queueDeclare(rpcQueueName,
                false, // queueDurable
                false, // queueExclusive
                false, // queueAutoDelete
                null); // standardArgs
    }

}
