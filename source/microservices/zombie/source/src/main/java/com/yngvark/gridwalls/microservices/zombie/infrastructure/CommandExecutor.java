package com.yngvark.gridwalls.microservices.zombie.infrastructure;

import com.google.inject.Inject;

import java.util.concurrent.ExecutorService;

public class CommandExecutor {
    private final ExecutorService executorService;
    private final StackTracePrinter stackTracePrinter;

    @Inject
    public CommandExecutor(ExecutorService executorService, StackTracePrinter stackTracePrinter) {
        this.executorService = executorService;
        this.stackTracePrinter = stackTracePrinter;
    }

    public void executeAsync(String commandId, Runnable command) {
        executorService.submit(() -> {
            System.out.println("Running command: " + commandId);
            command.run();
            System.out.println("Command finished: " + commandId);
        });
    }


}
