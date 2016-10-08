package zombie.lib;

import java.io.IOException;

public class ProcessStarter {
    public static Process startProcess(String path) throws IOException {
        System.out.println("Starting process: " + path);
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(path);
        return processBuilder.start();
    }
}
