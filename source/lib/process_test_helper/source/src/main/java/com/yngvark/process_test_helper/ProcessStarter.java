package com.yngvark.process_test_helper;

import java.io.IOException;

class ProcessStarter {
    public static Process startProcess(String... path) throws IOException {
        System.out.println("Starting process: " + path);
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(path);
        return processBuilder.start();
    }
}
