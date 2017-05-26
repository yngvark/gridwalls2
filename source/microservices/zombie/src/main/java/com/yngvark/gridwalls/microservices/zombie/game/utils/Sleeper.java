package com.yngvark.gridwalls.microservices.zombie.game.utils;

public class Sleeper {
    private final int timeToSleepInMillis;

    public Sleeper(int timeToSleepInMillis) {
        this.timeToSleepInMillis = timeToSleepInMillis;
    }

    public void sleep() {
        try {
            Thread.sleep(timeToSleepInMillis);
        } catch (InterruptedException e) {
            System.out.println("Sleeping was interruped. Details: " + e.getMessage());
        }
    }
}
