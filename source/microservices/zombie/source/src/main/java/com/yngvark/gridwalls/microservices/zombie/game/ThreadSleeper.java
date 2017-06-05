package com.yngvark.gridwalls.microservices.zombie.game;

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
