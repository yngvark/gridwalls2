package com.yngvark.gridwalls.microservices.zombie.netcom;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.RpcClient;
import com.yngvark.gridwalls.microservices.zombie.gamelogic.GameConfig;
import com.yngvark.gridwalls.microservices.zombie.infrastructure.ICanAbortOnSignal;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class FetchGameConfigRpcOld implements ICanAbortOnSignal {
    private static final String RPC_QUEUE_NAME = "rpc_queue";

    private Channel channel;

    public GameConfig getGameConfigFromServer() throws IOException, TimeoutException {
        channel = connection.createChannel();
        channel.queueDeclare(RPC_QUEUE_NAME, true, false, true, null);

        RpcClient rpc = new RpcClient(channel, "", RPC_QUEUE_NAME);
        System.out.println("Receiving game config.");
        String gameConfigTxt = rpc.stringCall("getGameConfig");
        System.out.println("Response: " + gameConfigTxt);

        rpc.close();
        channel.close();
        return parseGameConfig(gameConfigTxt);
    }

    @Override
    public void startAborting() {
        System.out.println(getClass().getSimpleName() + ": Exit signal received");
        if (channel.isOpen()) {
            try {
                channel.close();
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
            }
        }
    }
}
