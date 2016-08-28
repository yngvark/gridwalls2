package com.yngvark.gridwalls.microservices.zombie;

import com.yngvark.gridwalls.microservices.zombie.commands.Version;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main4 {
    public static void main(String[] args) {
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
}
