package com.yngvark.gridwalls.microservices.zombie;

import com.google.inject.Inject;

public class GameRunnerInitializer {
    private final SystemInputCommandExecutor systemInputCommandExecutor;

    @Inject
    public GameRunnerInitializer(SystemInputCommandExecutor systemInputCommandExecutor) {
        this.systemInputCommandExecutor = systemInputCommandExecutor;
    }

    public void setupCommands() {
        systemInputCommandExecutor.handleCommand("version", () -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("Version: Zombie 0.1");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //System.out.println("Version: Zombie 0.1");
        });
    }
}
