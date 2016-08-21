package com.yngvark.gridwalls.microservices.zombie;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Main3 {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new ModuleConfig());
        GameRunner3 gameRunner = createGameRunner(injector);
        gameRunner.run();
    }

    private static GameRunner3 createGameRunner(Injector injector) {
        GameRunnerInitializer gameRunnerInitializer = injector.getInstance(GameRunnerInitializer.class);
        gameRunnerInitializer.setupCommands();

        return injector.getInstance(GameRunner3.class);
    }
}
