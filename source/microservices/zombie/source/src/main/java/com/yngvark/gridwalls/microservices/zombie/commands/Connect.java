package com.yngvark.gridwalls.microservices.zombie.commands;

public class Connect implements Command {

    @Override
    public void run() {
        System.out.println("Usage: Connect to <broker host>");
    }

    @Override
    public void run(String[] arguments) {
        System.out.println("Connecting to some host.");
    }
}
