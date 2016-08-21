package com.yngvark.gridwalls.microservices.zombie;

import com.google.inject.Inject;
import com.yngvark.gridwalls.microservices.zombie.commands.Version;

public class GameRunnerInitializer2 {
    private final CommandHandler commandHandler;

    @Inject
    public GameRunnerInitializer2(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    public void setupCommands() {
        commandHandler.addCommand("version", new Version());
    }
}
