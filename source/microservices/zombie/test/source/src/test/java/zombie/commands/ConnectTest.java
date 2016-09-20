package zombie.commands;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zombie.Config;
import zombie.lib.CommandExecutor;
import zombie.lib.CommandExecutorFactory;
import zombie.lib.ProcessKiller;
import zombie.lib.ProcessStarter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConnectTest {
    private Process process;
    private CommandExecutor commandExecutor;

    public void beforeEach() throws IOException {
        process = new ProcessStarter().startProcess(Config.PATH_TO_APP);
        commandExecutor = new CommandExecutorFactory().create(process);
    }

    public void afterEach() throws InterruptedException {
        new ProcessKiller().exitAndAssertExited(process);
    }

    @Test
    public void command_should_print_usage_if_no_argument_is_provided() throws IOException, InterruptedException {
        // Given
        beforeEach();

        // When
        List<String> response = commandExecutor.run("connect");
        commandExecutor.run("exit");

        // Then
        Optional<String> errorMessageFound = response.stream().filter((String line) -> line.contains("Usage: Connect")).findFirst();
        assertTrue(errorMessageFound.isPresent(), "Could not find expected response from command.");
        assertEquals("Usage: Connect to <broker host>", errorMessageFound.get());

        // Finally
        afterEach();
    }

    @Test
    public void command_should_print_usage_if_two_arguments_are_provided() throws IOException, InterruptedException {
        // Given
        beforeEach();

        // When
        List<String> response = commandExecutor.run("connect horse buffalo");
        commandExecutor.run("exit");

        // Then
        Optional<String> errorMessageFound = response.stream().filter((String line) -> line.contains("Usage: Connect to <broker host>")).findFirst();
        assertTrue(errorMessageFound.isPresent());

        // Finally
        afterEach();
    }

    @Test
    public void command_should_print_error_message_if_connect_fails() throws IOException, InterruptedException {
        // Given
        beforeEach();
        String host = "f29438fma9rmf9a84mgf9a84gn9agaknzxg";

        // When
        List<String> response = commandExecutor.run("connect " + host);
        commandExecutor.run("exit");

        // Then
        Optional<String> errorMessageFound = response.stream().filter((String line) -> line.contains("Connect failed to host: " + host)).findFirst();
        assertTrue(errorMessageFound.isPresent());

        // Finally
        afterEach();
    }

    @Test
    public void command_should_print_success_message_if_connect_succeeds() throws IOException, InterruptedException {
        // Given
        beforeEach();

        // When
        List<String> response = commandExecutor.run("connect " + Config.BROKER_HOST);
        commandExecutor.run("exit");

        // Then
        Optional<String> errorMessageFound = response.stream().filter((String line) -> line.contains("Connected.")).findFirst();
        assertTrue(errorMessageFound.isPresent());

        // Finally
        afterEach();
    }
    
    @Test
    public void command_should_tell_if_already_connected() throws IOException, InterruptedException {
        // Given
        beforeEach();

        // When
        List<String> response = commandExecutor.run("connect " + Config.BROKER_HOST);
        commandExecutor.run("exit");

        // Then
        Optional<String> errorMessageFound = response.stream().filter((String line) -> line.contains("Already connected.")).findFirst();
        assertTrue(errorMessageFound.isPresent());

        // Finally
        afterEach();
    }
}
