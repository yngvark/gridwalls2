package com.yngvark.named_piped_app_runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

public class ProcessStarter {
    private static final Logger logger = LoggerFactory.getLogger(ProcessStarter.class);

    public static Process startProcess(String... path) throws IOException {
        logger.info("Starting process: {}", Arrays.toString(path), ", ");
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(path);
        return processBuilder.start();
    }
}
