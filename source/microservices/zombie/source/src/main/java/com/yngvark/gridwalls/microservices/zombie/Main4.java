package com.yngvark.gridwalls.microservices.zombie;

import com.yngvark.gridwalls.microservices.zombie.commands.Version;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main4 {
    public static void main(String[] args) {
//        Injector injector = Guice.createInjector(new ModuleConfig());
//        GameRunner4 gameRunner = createGameRunnerWithInjector(injector);

        createGameRunner();
    }

    private static void createGameRunner() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        StackTracePrinter stackTracePrinter = new StackTracePrinter();

        CommandHandler commandHandler = new CommandHandler(
                new CommandExecutor(
                        executorService,
                        stackTracePrinter
                ));
        commandHandler.addCommand("version", new Version());

        GameRunner4 gameRunner = new GameRunner4(
                new SystemInReader(
                        stackTracePrinter,
                        commandHandler
                ),
                executorService,
                new ExecutorServiceExiter(stackTracePrinter),
                stackTracePrinter
        );

        gameRunner.run();
    }

    //    private static GameRunner4 createGameRunnerWithInjector(Injector injector) {
//        GameRunnerInitializer2 gameRunnerInitializer = injector.getInstance(GameRunnerInitializer2.class);
//        gameRunnerInitializer.setupCommands();
//        return injector.getInstance(GameRunner4.class);
//    }
}
