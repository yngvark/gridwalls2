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

    @Test
    public void should_be_able_to_exit_2() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("../../container/app/bin/zombie");
        Process process = processBuilder.start();

        BufferedWriter appInputStream = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        BufferedReader appOutStream = new BufferedReader(new InputStreamReader(process.getInputStream()));

        new CommandExecutor(appInputStream, appOutStream).run("exit");

        boolean processHasExited = process.waitFor(3, TimeUnit.SECONDS);
        assertTrue(processHasExited);
    }

    @Test
    public void should_get_version() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("../../container/app/bin/zombie");
        Process process = processBuilder.start();

        BufferedWriter appInputStream = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        BufferedReader appOutStream = new BufferedReader(new InputStreamReader(process.getInputStream()));

        CommandExecutor commandExecutor = new CommandExecutor(appInputStream, appOutStream);
        List<String> response = commandExecutor.run("version");
        commandExecutor.run("exit");

        boolean versionOutputFound = response.stream().anyMatch((String line) -> line.contains("Version: Zombie"));
        assertTrue(versionOutputFound);

        boolean processHasExited = process.waitFor(3, TimeUnit.SECONDS);
        assertTrue(processHasExited);
    }

    @Test
    public void should_be_able_to_exit() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("../../container/app/bin/zombie");
        Process process = processBuilder.start();

        BufferedWriter appInputStream = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        BufferedReader appOutStream = new BufferedReader(new InputStreamReader(process.getInputStream()));

        while (true) {
            String line = appOutStream.readLine();

            if (line == null)
                break;

            System.out.println("\"" + line + "\"");

            if (line.contains("Enter something:")) {
                write(appInputStream, "exit");
                break;
            }
        }

        StringBuilder commandResponse = new StringBuilder();
        while (true) {
            String line = appOutStream.readLine();

            if (line == null)
                break;

            commandResponse.append(String.format(line + "%n"));
            System.out.println("\"" + line + "\"");
        }

        String response = commandResponse.toString();

        String[] lines = response.split(String.format("%n"));
        assertEquals("Exiting gracefully was successful.", lines[lines.length - 1]);

        boolean processHasExited = process.waitFor(3, TimeUnit.SECONDS);
        assertTrue(processHasExited);
    }

    @Test
    public void should_read_version() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("../../container/app/bin/zombie");
        Process ms = processBuilder.start();

        BufferedWriter appInputStream = new BufferedWriter(new OutputStreamWriter(ms.getOutputStream()));
        BufferedReader appOutStream = new BufferedReader(new InputStreamReader(ms.getInputStream()));

        System.out.println("Starting.");
        while (true) {
            String line = appOutStream.readLine();
            System.out.println("- " + line);

            if (line == null)
                break;

            if (line.contains("Enter something:")) {
                write(appInputStream, "version");
                break;
            }
        }

        System.out.println("Reading next");
        StringBuilder commandResponse = new StringBuilder();
        while (true) {
            String line = appOutStream.readLine();
            commandResponse.append(String.format(line + "%n"));
            System.out.println("- " + line);

            if (line == null)
                break;

            if (line.contains("Enter something:")) {
                write(appInputStream, "exit");
                break;
            }
        }

        System.out.println("Done");

        System.out.println("Command response:");
        System.out.println(commandResponse.toString());

        assertTrue(commandResponse.toString().contains("Version: Zombie"));

        boolean processHasExited = ms.waitFor(3, TimeUnit.SECONDS);
        assertTrue(processHasExited);
    }


    private void write(BufferedWriter appInputStream, String text) throws IOException {
        System.out.println("Sending: " + text);
        appInputStream.write(String.format(text + "%n"));
        appInputStream.flush();
    }


    @Test
    public void should_exit_withing_10_seconds_when_not_table_to_connect() { // how about logging?

    }

}

