package com.yngvark.gridwalls.microservices.zombie.game;

import com.yngvark.gridwalls.netcom.gameconfig.GameConfig;
import com.yngvark.gridwalls.netcom.gameconfig.GameConfigFetcher;

public class GameRunner {
    private final GameConfigFetcher gameConfigFetcher;
    private final GameLoopRunner gameRunnerLoop;

    public GameRunner(GameConfigFetcher gameConfigFetcher, GameLoopRunner gameRunnerLoop) {
        this.gameConfigFetcher = gameConfigFetcher;
        this.gameRunnerLoop = gameRunnerLoop;
    }

    public void run() {
        GameConfig gameConfig = gameConfigFetcher.getGameConfigFromServer();
        gameRunnerLoop.run(gameConfig);
    }
}
