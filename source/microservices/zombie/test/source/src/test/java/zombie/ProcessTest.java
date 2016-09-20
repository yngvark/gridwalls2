package zombie;

import org.junit.jupiter.api.Test;
import zombie.lib.CommandExecutor;
import zombie.lib.ProcessKiller;
import zombie.lib.ProcessStarter;
import zombie.lib.StdOutCharReader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProcessTest {
    @Test
    public void should_be_able_to_exit() throws IOException, InterruptedException {
        // Given
        Process process = new ProcessStarter().startProcess(Config.PATH_TO_APP);
        BufferedWriter appInputStream = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        BufferedReader appOutStream = new BufferedReader(new InputStreamReader(process.getInputStream()));

        // When
        new CommandExecutor(appInputStream, appOutStream).run("exit");

        // Then
        new ProcessKiller().exitAndAssertExited(process);
    }

    @Test
    public void should_be_able_to_kill_process_within_6_seconds() throws Exception {
        if (!System.getProperty("os.name").equals("Linux")) {
            System.out.println("WARNING: Test in class " + getClass().getName() + " did not run because it is dependent on Linux");
            return;
        }

        // Given
        Process process = new ProcessStarter().startProcess(Config.PATH_TO_APP);
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
        new ProcessKiller().exitAndAssertExited(process);
    }

}

