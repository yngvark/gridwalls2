package com.yngvark.gridwalls.microservices.zombie.game;

import com.yngvark.gridwalls.microservices.zombie.game.move.Move;

class GameLogic {
    private final  Sleeper sleeper;
    private final Serializer serializer;

    public GameLogic(Sleeper sleeper, Serializer serializer) {
        this.sleeper = sleeper;
        this.serializer = serializer;
    }

    public String nextMsg() {
        return serializer.serialize(new Move(3, 3), Move.class);
    }

    public void messageReceived(String msg) {

    }
}
