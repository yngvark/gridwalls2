package zombie.process_test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandExecutor {
    private final BufferedWriter appInputStream;
    private final BufferedReader appOutStream;
    private boolean waitingForCommand = false;

    public CommandExecutor(BufferedWriter appInputStream, BufferedReader appOutStream) {
        this.appInputStream = appInputStream;
        this.appOutStream = appOutStream;
    }

    public List<String> run(String command) throws IOException {
        boolean recordResponse = false;

        if (waitingForCommand) {
            write(appInputStream, command);
            recordResponse = true;
        }

        StringBuilder commandResponse = new StringBuilder();
        while (true) {
            String line = appOutStream.readLine();

            if (line == null)
                break;

            System.out.println("\"" + line + "\"");

            if (recordResponse)
                commandResponse.append(String.format(line + "%n"));

            if (line.contains("Enter something:") && recordResponse) {
                waitingForCommand = true;
                break;
            }

            if (line.contains("Enter something:")) {
                write(appInputStream, command);
                recordResponse = true;
            }
        }

        String response = commandResponse.toString();
        String[] lines = response.split(String.format("%n"));

        return Arrays.asList(lines);
    }


    private void write(BufferedWriter appInputStream, String text) throws IOException {
        System.out.println("Sending: " + text);
        appInputStream.write(String.format(text + "%n"));
        appInputStream.flush();
    }

}
