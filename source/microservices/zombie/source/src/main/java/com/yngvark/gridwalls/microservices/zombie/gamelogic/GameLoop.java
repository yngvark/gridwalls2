package com.yngvark.gridwalls.microservices.zombie.gamelogic;

import com.yngvark.gridwalls.microservices.zombie.infrastructure.GameErrorHandler;

public class GameLoop {
    private final ZombiesController zombiesController;
    private final GameErrorHandler gameErrorHandler;

    public GameLoop(ZombiesController zombiesController, GameErrorHandler gameErrorHandler) {
        this.zombiesController = zombiesController;
        this.gameErrorHandler = gameErrorHandler;
    }

    public void run(GameConfig gameConfig) {
        int i = 0;
        while (i < 10) {
            i++;
            try {
                zombiesController.nextTurn();
            } catch (Throwable e) {
                gameErrorHandler.handle(e);
                System.out.println("Aborting due to errors. Details: " + gameErrorHandler.getErrors());
                break;
            }
        }
    }
}
