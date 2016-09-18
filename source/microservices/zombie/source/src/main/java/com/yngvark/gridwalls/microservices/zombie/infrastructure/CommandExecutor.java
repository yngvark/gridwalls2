package com.yngvark.gridwalls.microservices.zombie.infrastructure;

import com.google.inject.Inject;
import com.yngvark.gridwalls.microservices.zombie.commands.Command;

import java.util.concurrent.ExecutorService;

public class CommandExecutor {
    private final ExecutorService executorService;

    @Inject
    public CommandExecutor(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public void executeAsync(String commandId, Command command) {
        executeAsync(commandId, command, new String[0]);
    }

    public void executeAsync(String commandId, Command command, String[] arguments) {
        executorService.submit(() -> {
            System.out.println("Running command: " + commandId);
            command.run(arguments);
            System.out.println("Command finished: " + commandId);
        });
    }


}
