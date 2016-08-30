package zombie;

import org.junit.jupiter.api.Test;
import sun.misc.IOUtils;
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
        Process process = startProcess(PATH_TO_APP);

        BufferedWriter appInputStream = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        BufferedReader appOutStream = new BufferedReader(new InputStreamReader(process.getInputStream()));

        // When
        new CommandExecutor(appInputStream, appOutStream).run("exit");

        // Then
        exitAndAssertExited(process);
    }

    private Process startProcess(String path) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(path);
        return processBuilder.start();
    }

    private void exitAndAssertExited(Process process) throws InterruptedException {
        boolean processHasExited = process.waitFor(3, TimeUnit.SECONDS);
        assertTrue(processHasExited);
    }

    @Test
    public void should_be_able_to_force_exit_within_3_seconds() throws IOException, InterruptedException {
        // Given
        Process process = startProcess(PATH_TO_APP);

        // When
        process.destroyForcibly();

        // Then
        exitAndAssertExited(process);

        assert false; // This test doesn't quite work I think.
    }

    @Test
    public void should_get_version() throws IOException, InterruptedException {
        // Given
        Process process = startProcess(PATH_TO_APP);

        BufferedWriter appInputStream = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        BufferedReader appOutStream = new BufferedReader(new InputStreamReader(process.getInputStream()));

        CommandExecutor commandExecutor = new CommandExecutor(appInputStream, appOutStream);

        // When
        List<String> response = commandExecutor.run("version");
        commandExecutor.run("exit");

        // Then
        boolean versionOutputFound = response.stream().anyMatch((String line) -> line.contains("Version: Zombie"));
        assertTrue(versionOutputFound);

        exitAndAssertExited(process);
    }

    @Test
    public void should_exit_withing_10_seconds_when_not_table_to_connect() { // how about logging?

    }

}

