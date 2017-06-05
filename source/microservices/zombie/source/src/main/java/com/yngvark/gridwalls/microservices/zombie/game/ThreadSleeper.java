package com.yngvark.gridwalls.microservices.zombie.game;

class ThreadSleeper implements Sleeper {
    @Override
    public void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
