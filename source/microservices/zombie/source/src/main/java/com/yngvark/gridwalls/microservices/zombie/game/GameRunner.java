package com.yngvark.gridwalls.microservices.zombie.game;

import com.yngvark.gridwalls.netcom.gameconfig.GameConfig;
import com.yngvark.gridwalls.netcom.gameconfig.GameConfigFetcher;

public class GameRunner implements ICanStop {
    private final GameConfigFetcher gameConfigFetcher;
    private final GameLoopRunner gameRunnerLoop;

    public GameRunner(GameConfigFetcher gameConfigFetcher, GameLoopRunner gameRunnerLoop) {
        this.gameConfigFetcher = gameConfigFetcher;
        this.gameRunnerLoop = gameRunnerLoop;
    }

    public void run() {
        startConsumingEvents();
        startProducingEvents();
    }

    private void startConsumingEvents() {

    }

    private void startProducingEvents() {
        GameConfig gameConfig = gameConfigFetcher.getGameConfigFromServer();
        gameRunnerLoop.run(gameConfig);
    }

    @Override
    public void stopAndWaitUntilStopped() {
        gameConfigFetcher.stopAndWaitUntilStopped();
        gameRunnerLoop.stopAndWaitUntilStopped();
    }
}
