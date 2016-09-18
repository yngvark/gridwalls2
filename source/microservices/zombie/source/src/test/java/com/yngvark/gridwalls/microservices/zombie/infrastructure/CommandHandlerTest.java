package com.yngvark.gridwalls.microservices.zombie.infrastructure;

import com.yngvark.gridwalls.microservices.zombie.commands.Command;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CommandHandlerTest {
    @Test
    public void should_do_nothing_when_command_is_not_found() throws Exception {
        // Given
        CommandExecutor commandExecutor = Mockito.mock(CommandExecutor.class);
        CommandHandler commandHandler = new CommandHandler(commandExecutor);

        // When
        commandHandler.handleInput("some_unknown_command");

        // Then
        verify(commandExecutor, times(0)).executeAsync(any(String.class), any(Command.class));
    }

    @Test
    public void should_run_command_without_arguments() throws Exception {
        // Given
        CommandExecutor commandExecutor = Mockito.mock(CommandExecutor.class);
        CommandHandler commandHandler = new CommandHandler(commandExecutor);
        commandHandler.addCommand("version", new EmptyCommand());

        // When
        commandHandler.handleInput("version");

        // Then
        verify(commandExecutor).executeAsync(eq("version"), any(EmptyCommand.class));
    }

    @Test
    public void should_split_command_and_arguments() throws Exception {
        // Given
        CommandExecutor commandExecutor = Mockito.mock(CommandExecutor.class);
        CommandHandler commandHandler = new CommandHandler(commandExecutor);
        commandHandler.addCommand("connect", new EmptyCommand());

        // When
        commandHandler.handleInput("connect someHost");

        // Then
        verify(commandExecutor).executeAsync(eq("connect"), any(EmptyCommand.class), eq(new String[] { "someHost" }));
    }

    class EmptyCommand implements Command {
        @Override public void run() { }
        @Override public void run(String[] arguments) { }
    }
}