package com.yngvark.gridwalls.netcom;

import com.yngvark.gridwalls.microservices.zombie.gamelogic.GameConfig;
import com.yngvark.gridwalls.microservices.zombie.infrastructure.StackTracePrinter;

import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class GameConfigFetcher {
    private static final String RPC_QUEUE_NAME = "rpc_queue";

    private final ExecutorService executorService;
    private final StackTracePrinter stackTracePrinter;
    private final Netcom netcom;
    private final GameConfigDeserializer gameConfigDeserializer;

    public GameConfigFetcher(
            ExecutorService executorService,
            StackTracePrinter stackTracePrinter,
            Netcom netcom,
            GameConfigDeserializer gameConfigDeserializer) {
        this.executorService = executorService;
        this.stackTracePrinter = stackTracePrinter;
        this.netcom = netcom;
        this.gameConfigDeserializer = gameConfigDeserializer;
    }

    public Optional<GameConfig> getGameConfigFromServer() {
        Future<RpcResult> rpcFuture = executorService.submit(() -> netcom.rpcCall(RPC_QUEUE_NAME, "getGameConfig"));

        RpcResult gameConfigRpcResult;
        try {
            System.out.println("Fetching game configuration.");
            gameConfigRpcResult = rpcFuture.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | CancellationException | TimeoutException e) {
            stackTracePrinter.print("Error while getting game configuration. Exiting", e);
            return Optional.empty();
        }

        if (gameConfigRpcResult.failed()) {
            System.out.println("RPC call for game configuration failed. Details: " + gameConfigRpcResult.getFailedInfo());
            return Optional.empty();
        }

        String gameConfigRespose = gameConfigRpcResult.getRpcResponse();
        GameConfig gameConfig = gameConfigDeserializer.deserialize(gameConfigRespose);

        return Optional.of(gameConfig);
    }
}
