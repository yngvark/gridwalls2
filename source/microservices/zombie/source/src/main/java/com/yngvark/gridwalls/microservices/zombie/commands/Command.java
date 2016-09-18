package com.yngvark.gridwalls.microservices.zombie.commands;

public interface Command {
    void run();
    void run(String[] arguments);
}
