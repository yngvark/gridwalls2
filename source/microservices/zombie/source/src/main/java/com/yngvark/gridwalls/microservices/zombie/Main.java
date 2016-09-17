package com.yngvark.gridwalls.microservices.zombie;

import com.yngvark.gridwalls.microservices.zombie.commands.Version;
import com.yngvark.gridwalls.microservices.zombie.infrastructure.CommandExecutor;
import com.yngvark.gridwalls.microservices.zombie.infrastructure.CommandHandler;
import com.yngvark.gridwalls.microservices.zombie.infrastructure.ExecutorServiceExiter;
import com.yngvark.gridwalls.microservices.zombie.infrastructure.GameRunner;
import com.yngvark.gridwalls.microservices.zombie.infrastructure.StackTracePrinter;
import com.yngvark.gridwalls.microservices.zombie.infrastructure.SystemInReader;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
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

        GameRunner gameRunner = new GameRunner(
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
