package com.yngvark.gridwalls.microservices.zombie.commands;

public class Version implements Runnable {

    @Override
    public void run() {
        System.out.println("Version: Zombie 0.1");
    }
}
