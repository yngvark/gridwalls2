package zombie.process_test;

import java.io.BufferedReader;
import java.io.IOException;

public class StdOutCharReader {
    private final BufferedReader appOutStream;

    public StdOutCharReader(BufferedReader appOutStream) {
        this.appOutStream = appOutStream;
    }

    public String readUntilEnd() throws IOException {
        StringBuilder commandResponse = new StringBuilder();
        while (true) {
            char c = (char) appOutStream.read();
            if (c == -1 || c == 65535)
                return commandResponse.toString();

            log(c);

            commandResponse.append(c);
        }
    }

    private void log(char c) {
        System.out.print(c);
    }
}
