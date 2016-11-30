package com.yngvark.gridwalls.microservices.zombie.game;

import com.yngvark.gridwalls.microservices.zombie.game.utils.GameErrorHandler;
import com.yngvark.gridwalls.microservices.zombie.game.utils.Sleeper;
import com.yngvark.gridwalls.netcom.gameconfig.GameConfig;

public class GameLoop {
    private final GameConfig gameConfig;
    private final ZombiesController zombiesController;
    private final GameErrorHandler gameErrorHandler;
    private final Sleeper sleeper;

    private boolean runLoop = true;

    public GameLoop(GameConfig gameConfig, ZombiesController zombiesController,
            GameErrorHandler gameErrorHandler, Sleeper sleeper) {
        this.gameConfig = gameConfig;
        this.zombiesController = zombiesController;
        this.gameErrorHandler = gameErrorHandler;
        this.sleeper = sleeper;
    }

    public void loop() {
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

            sleeper.sleep();
        }
    }

    public void stopLoop() {
        runLoop = false;
    }
}
