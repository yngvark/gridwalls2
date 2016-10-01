package com.yngvark.gridwalls.microservices.zombie;

import com.yngvark.gridwalls.microservices.zombie.infrastructure.CommandExecutor;
import com.yngvark.gridwalls.microservices.zombie.infrastructure.CommandHandler;
import com.yngvark.gridwalls.microservices.zombie.infrastructure.ExecutorServiceExiter;
import com.yngvark.gridwalls.microservices.zombie.infrastructure.ProcessRunner;
import com.yngvark.gridwalls.microservices.zombie.infrastructure.StackTracePrinter;

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
                new CommandExecutor(executorService));

        ProcessRunner processRunner = new ProcessRunner(
                executorService,
                new ExecutorServiceExiter(stackTracePrinter),
                stackTracePrinter
        );

        processRunner.run();
    }

}
