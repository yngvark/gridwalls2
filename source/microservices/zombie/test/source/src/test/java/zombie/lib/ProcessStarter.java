package zombie.lib;

import java.io.IOException;

public class ProcessStarter {
    public Process startProcess(String path) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(path);
        return processBuilder.start();
    }
}
