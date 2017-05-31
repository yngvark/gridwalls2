package com.yngvark.gridwalls.microservices.zombie.game;

import com.yngvark.gridwalls.microservices.zombie.game.utils.GameErrorHandler;
import com.yngvark.gridwalls.microservices.zombie.game.utils.Sleeper;
import com.yngvark.gridwalls.netcom.gameconfig.GameConfig;

public class GameLoopFactory {
    private final ZombiesController zombiesController;
    private final GameErrorHandler gameErrorHandler;

    public GameLoopFactory(ZombiesController zombiesController, GameErrorHandler gameErrorHandler) {
        this.zombiesController = zombiesController;
        this.gameErrorHandler = gameErrorHandler;
    }

    public GameLoop create(GameConfig gameConfig) {
        return new GameLoop(gameConfig, zombiesController, gameErrorHandler, new Sleeper(gameConfig.getSleepTimeMillisBetweenTurns()));
    }
}
