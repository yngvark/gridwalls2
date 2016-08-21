package com.yngvark.gridwalls.microservices.zombie;

import com.google.inject.Inject;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler {
    private final CommandExecutor commandExecutor;
    private final Map<String, Runnable> commands = new HashMap();

    @Inject
    public CommandHandler(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    public void addCommand(String command, Runnable commandRunnable) {
        System.out.println("Adding command: " + command);
        commands.put(command, commandRunnable);
    }

    public void handle(String command) {
        Runnable commandRunnable = commands.get(command);

        if (commandRunnable == null) {
            System.out.println("Unknown command: " + command);
            return;
        }

        commandExecutor.executeAsync(command, commandRunnable);
    }
}
