package com.yngvark.communicate_through_named_pipes;

import org.slf4j.Logger;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.slf4j.LoggerFactory.getLogger;

public class FileExistsWaiter {
    private final Logger logger = getLogger(getClass());
    private final RetrySleeper retrySleeper;

    public FileExistsWaiter(RetrySleeper retrySleeper) {
        this.retrySleeper = retrySleeper;
    }

    public void waitUntilFileExists(String file) {
        while (!Files.exists(Paths.get(file))) {
            logger.warn("Could not find file. Attempting again soon.");
            try {
                retrySleeper.sleep();
            } catch (InterruptedException ie) {
                throw new RuntimeException(ie);
            }
        }
    }
}
