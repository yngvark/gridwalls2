package com.yngvark.gridwalls.microservices.zombie.gamelogic;

import com.yngvark.gridwalls.microservices.zombie.infrastructure.StackTracePrinter;
import com.yngvark.gridwalls.microservices.zombie.netcom.FetchGameConfigRpc;
import com.yngvark.gridwalls.microservices.zombie.netcom.FetchGameConfigRpcOld;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class GameRunner {
    private final FetchGameConfigRpc fetchGameConfigRpc;
    private final GameLoop gameLoop;
    private final ExecutorService executorService;
    private final StackTracePrinter stackTracePrinter;


    public void run() {
        Future<GameConfig> gameConfigFuture = executorService.submit(() -> fetchGameConfigRpc.getGameConfigFromServer());
        GameConfig gameConfig = null;
        try {
            System.out.println("Fetching game configuration.");
            gameConfig = gameConfigFuture.get();
            gameConfigFuture.
        } catch (InterruptedException | ExecutionException | CancellationException e) {
            stackTracePrinter.print("Could not get game configuration. Exiting", e);
            return;
        }

        gameLoop.run(gameConfig);


        disconnectIfConnected();
        exit();
    }
}
