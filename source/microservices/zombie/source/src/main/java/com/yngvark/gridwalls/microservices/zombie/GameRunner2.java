package com.yngvark.gridwalls.microservices.zombie;

import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeoutException;

public class GameRunner2 {
    private RabbitMqConnector rabbitMqConnector;

    public void run() {
        final Connection connection;

        try {
            connection = rabbitMqConnector.connect();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (TimeoutException e) {
            e.printStackTrace();
            return;
        }

        runConnected(connection);

        try {
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    private void runConnected(Connection connection) {
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {

        });
    }

}
