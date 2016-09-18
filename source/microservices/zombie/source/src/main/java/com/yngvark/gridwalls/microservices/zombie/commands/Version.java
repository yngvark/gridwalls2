package com.yngvark.gridwalls.microservices.zombie.commands;

public class Version implements Command {
    @Override
    public void run() {
        doRun();
    }

    private void doRun() {
        System.out.println("Version: Zombie 0.1");
    }

    @Override
    public void run(String[] arguments) {
        run();
    }
}
