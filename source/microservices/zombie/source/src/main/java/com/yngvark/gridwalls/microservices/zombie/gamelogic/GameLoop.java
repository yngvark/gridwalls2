package com.yngvark.gridwalls.microservices.zombie.gamelogic;

public class GameLoop {
    private ZombiesController zombiesController;

    public void run(GameConfig gameConfig) {
        int i = 0;
        while (i < 10) {
            i++;
            zombiesController.nextTurn();
        }
    }
}
