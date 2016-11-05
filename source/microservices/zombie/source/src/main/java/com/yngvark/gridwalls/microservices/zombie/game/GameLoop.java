package com.yngvark.gridwalls.microservices.zombie.game;

import com.yngvark.gridwalls.microservices.zombie.game.utils.GameErrorHandler;
import com.yngvark.gridwalls.netcom.gameconfig.GameConfig;

public class GameLoop {
    private final ZombiesController zombiesController;
    private final GameErrorHandler gameErrorHandler;

    private boolean runLoop = true;

    public GameLoop(ZombiesController zombiesController, GameErrorHandler gameErrorHandler) {
        this.zombiesController = zombiesController;
        this.gameErrorHandler = gameErrorHandler;
    }

    public void run(GameConfig gameConfig) {
        int i = 0;
        while (i < 10 && runLoop) {
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

    public void stop() {
        runLoop = false;
    }
}
