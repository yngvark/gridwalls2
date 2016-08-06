package com.yngvark.gridwalls.microservices.zombie;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.RpcClient;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

class GameConfigFetcher implements ICanExitOnSignal {
    private static final String RPC_QUEUE_NAME = "rpc_queue";

    private Channel channel;

    public GameConfig getGameConfigFromServer(Connection connection) throws IOException, TimeoutException {
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

    private GameConfig parseGameConfig(String serialized) {
        // "[GameInfo] mapHeight=10 mapWidth=10"
        int heightStart = serialized.indexOf("mapHeight=");
        int widthStart = serialized.indexOf("mapWidth=");

        String heightTxt = serialized.substring(heightStart + "mapHeight=".length(), widthStart - 1);
        String widthTxt = serialized.substring(widthStart + "mapWidth=".length());

        int mapHeight = Integer.parseInt(heightTxt);
        int mapWidth = Integer.parseInt(widthTxt);

        return GameConfig.builder()
                .mapHeight(mapHeight)
                .mapWidth(mapWidth)
                .build();
    }

    @Override
    public void exitSignalReceived() throws Exception {
        System.out.println(getClass().getSimpleName() + ": Exit signal received");
        if (channel.isOpen())
            channel.close();
    }
}
