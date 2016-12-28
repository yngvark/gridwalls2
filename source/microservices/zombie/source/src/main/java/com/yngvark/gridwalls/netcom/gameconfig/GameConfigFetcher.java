package com.yngvark.gridwalls.netcom.gameconfig;

import com.yngvark.gridwalls.microservices.zombie.game.ICanStop;
import com.yngvark.gridwalls.netcom.Netcom;
import com.yngvark.gridwalls.netcom.rpc.RpcResult;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class GameConfigFetcher implements ICanStop {
    private static final String RPC_QUEUE_NAME = "rpc_queue";

    private final ExecutorService executorService;
    private final Netcom netcom;
    private final Deserializer deserializer;

    private Future<RpcResult> rpcFuture;

    public GameConfigFetcher(ExecutorService executorService, Netcom netcom, Deserializer deserializer) {
        this.executorService = executorService;
        this.netcom = netcom;
        this.deserializer = deserializer;
    }

    public GameConfig getGameConfigFromServer() {
        System.out.println("Fetching game configuration.");
        RpcResult gameConfigRpcResult = netcom.rpcCall(RPC_QUEUE_NAME, "getGameConfig");

        if (!gameConfigRpcResult.succeeded()) {
            throw new RuntimeException("RPC call for game configuration failed. Details: " + gameConfigRpcResult.getFailedInfo());
        }

        String gameConfigRespose = gameConfigRpcResult.getRpcResponse();
        GameConfig gameConfig = deserializer.deserialize(gameConfigRespose);

        return gameConfig;
    }

    @Override
    public void stopAndWaitUntilStopped() {
        if (rpcFuture != null) {
            rpcFuture.cancel(true);
            System.out.println("Stopping " + getClass().getSimpleName());
        }
        System.out.println("Stopped " + getClass().getSimpleName());
    }
}
