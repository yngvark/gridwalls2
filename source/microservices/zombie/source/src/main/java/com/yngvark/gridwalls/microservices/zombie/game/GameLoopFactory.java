package com.yngvark.gridwalls.microservices.zombie.game;

import com.yngvark.gridwalls.netcom.gameconfig.GameConfig;

public class GameLoopFactory {
    private final ZombiesController zombiesController;

    public GameLoopFactory(ZombiesController zombiesController) {
        this.zombiesController = zombiesController;
    }

    public GameLoop create(GameConfig gameConfig) {
        return new GameLoop(gameConfig, zombiesController);
    }

}
