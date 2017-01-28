package com.yngvark.gridwalls.microservices.gameserver;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Main {
    public static void main(String[] args) {
        String rpcQueueName = "rpc_queue";

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("rabbithost");
        Connection connection =  null;
        try {
            connection = factory.newConnection();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        String serverResponse = "[GameInfo] mapWidth=10 mapWidth=10 sleepTimeMillisBetweenTurns=600";
        GameRpcServer gameRpcServer = new GameRpcServer(connection, rpcQueueName, (String request) -> serverResponse);
        gameRpcServer.run();

        try {
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
