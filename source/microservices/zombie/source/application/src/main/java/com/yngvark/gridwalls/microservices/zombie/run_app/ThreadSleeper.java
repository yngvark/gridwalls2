package com.yngvark.gridwalls.microservices.zombie.run_app;

import com.yngvark.gridwalls.microservices.zombie.run_game.Sleeper;

class ThreadSleeper implements Sleeper {
    @Override
    public void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
