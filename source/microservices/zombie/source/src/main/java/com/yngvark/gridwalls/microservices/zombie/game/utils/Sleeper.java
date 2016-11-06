package com.yngvark.gridwalls.microservices.zombie.game.utils;

public class Sleeper {
    public void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println("Sleeping was interruped. Details: " + e.getMessage());
        }
    }
}