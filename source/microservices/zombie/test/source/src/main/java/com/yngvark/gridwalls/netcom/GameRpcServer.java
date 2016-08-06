package com.yngvark.gridwalls.netcom;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.RpcServer;
import com.rabbitmq.client.StringRpcServer;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class GameRpcServer implements ICanRunAndStop {
    private final Connection connection;
    private final String queueName;
    private final RpcRequestHandler rpcRequestHandler;

    private Channel channel = null;
    private RpcServer rpcServer;

    private boolean runStarted = false;

    public GameRpcServer(Connection connection, String queueName, RpcRequestHandler rpcRequestHandler) {
        if (connection == null || queueName == null || rpcRequestHandler == null)
            throw new RuntimeException("All arguments must be non-null");

        if (queueName.isEmpty())
            throw new RuntimeException("Queue name cannot be empty");

        this.connection = connection;
        this.queueName = queueName;
        this.rpcRequestHandler = rpcRequestHandler;
    }

    public void run() {
        if (runStarted)
            throw new RuntimeException("Cannot run the server more than once.");
        runStarted = true;

        doRun();
    }

    private void doRun() {
        try {
            tryToRun();
        } catch (IOException e) {
            e.printStackTrace();
            stop();
        }
    }

    private void tryToRun() throws IOException {
        channel = connection.createChannel();
        initQueue();
        rpcServer = initRpcServer();

        System.out.println("RPC server for queue '" + queueName + "' entering mainloop.");
        rpcServer.mainloop();
        System.out.println("RPC server for queue '" + queueName + "' exited mainloop.");
    }

    private StringRpcServer initRpcServer() throws IOException {
        return new StringRpcServer(channel, queueName) {
            @Override
            public String handleStringCall(String request) {
                System.out.println("Received request: " + request);

                String response = rpcRequestHandler.handle(request);
                System.out.println("Response: " + response);

                return response;
            }
        };
    }

    private void initQueue() throws IOException {
        Map<String, Object> standardArgs = null;
        boolean queueDurable = false;
        boolean queueExclusive = true;
        boolean queueAutoDelete = true;
        channel.queueDeclare(queueName, queueDurable, queueExclusive, queueAutoDelete, standardArgs);
    }

    public void stop() {
        if (channel != null) {
            try {
                tryToStop();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }
    }

    private void tryToStop() throws IOException, TimeoutException {
        if (rpcServer != null)
            rpcServer.terminateMainloop();

        if (channel.isOpen())
            channel.close();

        channel = null;
    }
}