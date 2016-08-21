package com.yngvark.gridwalls.microservices.zombie;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Main4 {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new ModuleConfig());

        GameRunner4 gameRunner = createGameRunner(injector);
        gameRunner.run();
    }

    private static GameRunner4 createGameRunner(Injector injector) {
        GameRunnerInitializer2 gameRunnerInitializer = injector.getInstance(GameRunnerInitializer2.class);
        gameRunnerInitializer.setupCommands();

        return injector.getInstance(GameRunner4.class);
    }
}
