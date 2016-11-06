package com.yngvark.gridwalls.microservices.zombie.game;

import com.yngvark.gridwalls.netcom.gameconfig.GameConfig;

public class GameLoop {
    private final GameConfig gameConfig;
    private final ZombiesController zombiesController;

    public GameLoop(GameConfig gameConfig, ZombiesController zombiesController) {
        this.gameConfig = gameConfig;
        this.zombiesController = zombiesController;
    }

    public void nextTurn() {
        zombiesController.nextTurn();
    }

}
