package zombie;

import org.junit.jupiter.api.Test;
import zombie.process_test.CommandExecutor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProcessTest {
    private final static String PATH_TO_APP = "../../container/app/bin/zombie";

    @Test
    public void should_be_able_to_exit() throws IOException, InterruptedException {
        // Given
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(PATH_TO_APP);
        Process process = processBuilder.start();

        BufferedWriter appInputStream = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        BufferedReader appOutStream = new BufferedReader(new InputStreamReader(process.getInputStream()));

        // When
        new CommandExecutor(appInputStream, appOutStream).run("exit");

        // Then
        assertExited(process);
    }

    private void assertExited(Process process) throws InterruptedException {
        boolean processHasExited = process.waitFor(3, TimeUnit.SECONDS);
        assertTrue(processHasExited);
    }

    @Test
    public void should_get_version() throws IOException, InterruptedException {
        // Given
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(PATH_TO_APP);
        Process process = processBuilder.start();

        BufferedWriter appInputStream = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        BufferedReader appOutStream = new BufferedReader(new InputStreamReader(process.getInputStream()));

        CommandExecutor commandExecutor = new CommandExecutor(appInputStream, appOutStream);

        // When
        List<String> response = commandExecutor.run("version");
        commandExecutor.run("exit");

        // Then
        boolean versionOutputFound = response.stream().anyMatch((String line) -> line.contains("Version: Zombie"));
        assertTrue(versionOutputFound);

        assertExited(process);
    }

    @Test
    public void should_exit_withing_10_seconds_when_not_table_to_connect() { // how about logging?

    }

}

