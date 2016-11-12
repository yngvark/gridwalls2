package com.yngvark.gridwalls.microservices.zombie.game;

import com.yngvark.gridwalls.netcom.gameconfig.GameConfig;
import com.yngvark.gridwalls.netcom.gameconfig.GameConfigFetcher;

import java.util.Optional;

public class GameRunner {
    private final GameConfigFetcher gameConfigFetcher;
    private final GameLoopRunner gameRunnerLoop;

    public GameRunner(GameConfigFetcher gameConfigFetcher, GameLoopRunner gameRunnerLoop) {
        this.gameConfigFetcher = gameConfigFetcher;
        this.gameRunnerLoop = gameRunnerLoop;
    }

    public void run() {
        Optional<GameConfig> gameConfigOptional = gameConfigFetcher.getGameConfigFromServer();
        if (!gameConfigOptional.isPresent()) {
            System.out.println("Could not get game configuration.");
            return;
        }

        GameConfig gameConfig = gameConfigOptional.get();

        gameRunnerLoop.run(gameConfig);
    }
}
