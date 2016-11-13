package com.yngvark.gridwalls.netcom.gameconfig;

import com.yngvark.gridwalls.netcom.Netcom;
import com.yngvark.gridwalls.netcom.rpc.RpcResult;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class GameConfigFetcher {
    private static final String RPC_QUEUE_NAME = "rpc_queue";

    private final ExecutorService executorService;
    private final Netcom netcom;
    private final Deserializer deserializer;

    public GameConfigFetcher(ExecutorService executorService, Netcom netcom, Deserializer deserializer) {
        this.executorService = executorService;
        this.netcom = netcom;
        this.deserializer = deserializer;
    }

    public GameConfig getGameConfigFromServer() {
        Future<RpcResult> rpcFuture = executorService.submit(() -> netcom.rpcCall(RPC_QUEUE_NAME, "getGameConfig"));

        RpcResult gameConfigRpcResult;
        try {
            System.out.println("Fetching game configuration.");
            gameConfigRpcResult = rpcFuture.get(3, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | CancellationException | TimeoutException e) {
            throw new RuntimeException("Error while getting game configuration. Exiting", e);
        }

        if (!gameConfigRpcResult.succeeded()) {
            throw new RuntimeException("RPC call for game configuration failed. Details: " + gameConfigRpcResult.getFailedInfo());
        }

        String gameConfigRespose = gameConfigRpcResult.getRpcResponse();
        GameConfig gameConfig = deserializer.deserialize(gameConfigRespose);

        return gameConfig;
    }
}
