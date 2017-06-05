package com.yngvark.gridwalls.microservices.zombie.game;

import com.yngvark.gridwalls.microservices.zombie.game.Sleeper;

class NoSleeper implements Sleeper {
    @Override
    public void sleep() {
        // Do nothing
    }
}
