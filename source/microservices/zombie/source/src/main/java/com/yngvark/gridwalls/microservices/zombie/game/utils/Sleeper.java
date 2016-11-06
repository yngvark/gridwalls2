package com.yngvark.gridwalls.microservices.zombie.game.utils;

public class Sleeper {
    private final int millisToSleep;

    public Sleeper(int millisToSleep) {
        this.millisToSleep = millisToSleep;
    }

    public void sleep() {
        try {
            Thread.sleep(millisToSleep);
        } catch (InterruptedException e) {
            System.out.println("Sleeping was interruped. Details: " + e.getMessage());
            return;
        }
    }
}
