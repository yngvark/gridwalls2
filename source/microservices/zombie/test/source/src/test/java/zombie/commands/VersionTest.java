package zombie.commands;

import org.junit.jupiter.api.Test;
import zombie.Config;
import zombie.lib.CommandExecutor;
import zombie.lib.CommandExecutorFactory;
import zombie.lib.ProcessKiller;
import zombie.lib.ProcessStarter;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class VersionTest {

    @Test
    public void should_get_version() throws IOException, InterruptedException {
        // Given
        Process process = new ProcessStarter().startProcess(Config.PATH_TO_APP);
        CommandExecutor commandExecutor = new CommandExecutorFactory().create(process);

        // When
        List<String> response = commandExecutor.run("version");
        commandExecutor.run("exit");

        // Then
        boolean versionOutputFound = response.stream().anyMatch((String line) -> line.contains("Version: Zombie"));
        assertTrue(versionOutputFound);

        new ProcessKiller().exitAndAssertExited(process);
    }
}
