package com.yngvark.gridwalls.microservices.zombie.gamelogic;

import com.yngvark.gridwalls.netcom.GameConfigFetcher;

import java.util.Optional;

public class GameRunner {
    private static final String RPC_QUEUE_NAME = "rpc_queue";

    private final GameConfigFetcher gameConfigFetcher;
    private final GameLoop gameLoop;

    public GameRunner(GameConfigFetcher gameConfigFetcher, GameLoop gameLoop) {
        this.gameConfigFetcher = gameConfigFetcher;
        this.gameLoop = gameLoop;
    }

    public void run() {
        Optional<GameConfig> gameConfigOptional = gameConfigFetcher.getGameConfigFromServer();
        if (!gameConfigOptional.isPresent()) {
            System.out.println("Could not get game configuration.");
            return;
        }

        GameConfig gameConfig = gameConfigOptional.get();

        gameLoop.run(gameConfig);
    }
}
