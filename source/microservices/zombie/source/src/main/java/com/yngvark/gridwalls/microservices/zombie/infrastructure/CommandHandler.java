package com.yngvark.gridwalls.microservices.zombie.infrastructure;

import com.google.inject.Inject;
import com.yngvark.gridwalls.microservices.zombie.commands.Command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CommandHandler {
    private final CommandExecutor commandExecutor;
    private final Map<String, Command> commands = new HashMap();

    @Inject
    public CommandHandler(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    public void addCommand(String command, Command commandRunnable) {
        System.out.println("Adding command: " + command);
        commands.put(command, commandRunnable);
    }

    public void handleInput(String input) {
        String command = getCommandFrom(input);
        Command commandRunnable = commands.get(command);

        if (commandRunnable == null) {
            System.out.println("Unknown command: " + input);
            return;
        }

        String[] arguments = getArgumentsFrom(input);

        if (arguments.length == 0)
            commandExecutor.executeAsync(command, commandRunnable);
        else
            commandExecutor.executeAsync(command, commandRunnable, arguments);
    }

    private String getCommandFrom(String input) {
        return input.split(" ")[0];
    }

    private String[] getArgumentsFrom(String input) {
        String[] splitted = input.split(" ");
        return Arrays.copyOfRange(splitted, 1, splitted.length);
    }
}
