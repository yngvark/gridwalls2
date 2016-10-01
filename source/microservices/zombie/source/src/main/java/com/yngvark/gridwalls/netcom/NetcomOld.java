package com.yngvark.gridwalls.netcom;

import com.yngvark.gridwalls.microservices.zombie.Config;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class NetcomOld { // implements Broker
//    private final Config config;
//    private final OneTImeConnecter oneTImeConnecter;
//    private final RpcCaller rpcCaller;
//
//    private ConnectionWrapper ConnectionWrapper;
//
//    public NetcomOld(Config config, OneTImeConnecter oneTImeConnecter, RpcCaller rpcCaller) {
//        this.config = config;
//        this.oneTImeConnecter = oneTImeConnecter;
//        this.rpcCaller = rpcCaller;
//    }
//
//    private void tryToEnsureConnected() {
//        try {
//            ensureConnected();
//        } catch (ConnectException e) {
//            throw new RpcException("Could not connect to server", e);
//        }
//    }
//
//    private void ensureConnected() {
//        if (ConnectionWrapper.isConnected())
//            return;
//
//        ConnectAttempt connectAttempt;
//        for (int i = 0; i < 3; i++) {
//            System.out.println("Connecting to " + config.RABBITMQ_HOST + " (attempt " + i + ")");
//
//            connectAttempt = oneTImeConnecter.connect(config.RABBITMQ_HOST, 5000);
//
//            if (connectAttempt.succeeded()) {
//                System.out.println("Connected.");
//                ConnectionWrapper = connectAttempt.getConnection();
//                return;
//            } else {
//                System.out.println("Cannot connect. Reason: " + connectAttempt.getConnectFailedDetails());
//            }
//
//        }
//
//        throwConnectError();
//    }
//
//    private String tryToDoRpcCall(String rpcQueueName, String message) {
//        try {
//            return doRpcCall(rpcQueueName, message);
//        } catch (TimeoutException | IOException e) {
//            throw new RpcException("Could not do RPC-call.", e);
//        }
//    }
//
//    private String doRpcCall(String rpcQueueName, String message) throws IOException, TimeoutException {
//        return rpcCaller.rpcCall(ConnectionWrapper, rpcQueueName, message);
//
//    }
//
//    private void throwConnectError() {
//        throw new ConnectException("Could not connect to server.");
//    }
//
//
//    public synchronized void disconnectIfConnected() {
//        if (!isConnected)
//            return;
//
//        try {
//            ConnectionWrapper.close();
//        } catch (IOException e) {
//            stackTracePrinter.print("Got error while disconnecting. Continuing work.", e);
//        } finally {
//            isConnected = false;
//        }
//    }
}
