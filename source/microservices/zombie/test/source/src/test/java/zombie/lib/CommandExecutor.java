package zombie.lib;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CommandExecutor {
    private static final String ENTER_COMMAND_TEXT = "Enter something:";

    private final BufferedWriter appInputStream;
    private final BufferedReader appOutStream;

    private boolean hasInitialized = false;

    public CommandExecutor(BufferedWriter appInputStream, BufferedReader appOutStream) {
        this.appInputStream = appInputStream;
        this.appOutStream = appOutStream;
    }

    public List<String> run(String command) throws IOException {
        if (!hasInitialized)
            init();

        runCommand(command);

        StringBuilder commandResponse = new StringBuilder();
        while (true) {
            String line = appOutStream.readLine();
            if (line == null || line.startsWith(ENTER_COMMAND_TEXT))
                return createListBasedOnLineBreaks(commandResponse);

            log(line);

            commandResponse.append(String.format(line + "%n"));
        }
    }

    private void init() throws IOException {
        String line;
        do {
            line = appOutStream.readLine();
            log(line);
        } while (!line.startsWith(ENTER_COMMAND_TEXT));

        hasInitialized = true;
    }

    private void log(String line) {
        System.out.println("\"" + line + "\"");
    }

    private void runCommand(String text) throws IOException {
        System.out.println("Sending: " + text);
        appInputStream.write(String.format(text + "%n"));
        appInputStream.flush();
    }

    private List<String> createListBasedOnLineBreaks(StringBuilder commandResponse) {
        String response = commandResponse.toString();
        String[] lines = response.split(String.format("%n"));
        return Arrays.asList(lines);
    }

}
