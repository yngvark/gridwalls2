package com.yngvark.gridwalls.microservices.zombie;

import com.yngvark.gridwalls.microservices.zombie.gameloop.GameLoopRunner;

public class Main {
    public static void main(String[] args) {
        GameLoopRunner gameLoopRunner = GameLoopRunner.create(null, null);
        gameLoopRunner.run();
    }
}
