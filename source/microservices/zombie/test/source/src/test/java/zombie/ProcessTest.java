package zombie;

import org.junit.jupiter.api.Test;
import zombie.process_test.CommandExecutor;
import zombie.process_test.ProcessKiller;
import zombie.process_test.StdOutCharReader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
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
    public void should_be_able_to_kill_process_within_6_seconds() throws Exception {
        if (!System.getProperty("os.name").equals("Linux")) {
            System.out.println("WARNING: Test in class " + getClass().getName() + " did not run because it is dependent on Linux");
            return;
        }

        // Given
        Process process = startProcess(PATH_TO_APP);
        BufferedWriter appInputStream = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        BufferedReader appOutStream = new BufferedReader(new InputStreamReader(process.getInputStream()));
        new CommandExecutor(appInputStream, appOutStream).run("version");

        // When
        System.out.println("Running 'kill' on executable.");
        LocalTime startTime = LocalTime.now();
        ProcessKiller.killUnixProcess(process);

        // Then
        StdOutCharReader stdOutCharReader = new StdOutCharReader(appOutStream);
        stdOutCharReader.readUntilEnd(); // Will block until process has terminated.

        LocalTime endTime = LocalTime.now();
        long exitDurationMillis = ChronoUnit.MILLIS.between(startTime, endTime);
        System.out.println("Process exit time in milliseconds: " + exitDurationMillis);

        assertTrue(exitDurationMillis < 6000);
        exitAndAssertExited(process);
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
    public void should_exit_withing_10_seconds_when_not_able_to_connect() { // how about logging?

    }

}

