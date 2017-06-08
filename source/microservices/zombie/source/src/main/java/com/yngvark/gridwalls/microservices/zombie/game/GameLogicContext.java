package com.yngvark.gridwalls.microservices.zombie.game;

class GameLogicContext {
    private GameLogic currentGameLogic;

    public GameLogicContext(GameLogic currentGameLogic) {
        this.currentGameLogic = currentGameLogic;
    }

    public void setCurrentGameLogic(GameLogic currentGameLogic) {
        this.currentGameLogic = currentGameLogic;
    }

    public String nextMsg() {
        return currentGameLogic.nextMsg(this);
    }
}
