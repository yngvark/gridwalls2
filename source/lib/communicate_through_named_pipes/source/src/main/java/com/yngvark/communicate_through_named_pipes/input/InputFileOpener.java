package com.yngvark.communicate_through_named_pipes.input;

import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.slf4j.LoggerFactory.getLogger;

public class InputFileOpener {
    private final Logger logger = getLogger(getClass());

    private final RetrySleeper retrySleeper;
    private final String fifoInputFilename;

    public InputFileOpener(RetrySleeper retrySleeper, String fifoInputFilename) {
        this.retrySleeper = retrySleeper;
        this.fifoInputFilename = fifoInputFilename;
    }

    public InputFileReader openStream() throws FileNotFoundException {
        logger.info("Opening consume file... " + fifoInputFilename);

        while (!Files.exists(Paths.get(fifoInputFilename))) {
            try {
                logger.warn("Could not find file. Attempting again soon.");
                retrySleeper.sleep();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        FileInputStream fileInputStream = new FileInputStream(fifoInputFilename);
        logger.info("Opening consume file... done.");

        BufferedReader in = new BufferedReader(new InputStreamReader(fileInputStream));

        return new InputFileReader(in);
    }

}
