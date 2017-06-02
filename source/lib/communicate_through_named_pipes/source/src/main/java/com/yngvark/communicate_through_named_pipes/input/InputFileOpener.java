package com.yngvark.communicate_through_named_pipes.input;

import com.yngvark.communicate_through_named_pipes.RetrySleeper;
import com.yngvark.communicate_through_named_pipes.RetryWaiter;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import static org.slf4j.LoggerFactory.getLogger;

public class InputFileOpener {
    private final Logger logger = getLogger(getClass());
    private final String fifoInputFilename;

    public InputFileOpener(String fifoInputFilename) {
        this.fifoInputFilename = fifoInputFilename;
    }

    public InputFileReader openStream(RetrySleeper retrySleeper) {
        logger.info("Opening input file... " + fifoInputFilename);

        RetryWaiter retryWaiter = new RetryWaiter(retrySleeper);
        retryWaiter.waitUntilFileExists(fifoInputFilename);
        FileInputStream fileInputStream = openFileStream(fifoInputFilename);

        logger.info("Opening input file... done.");

        BufferedReader in = new BufferedReader(new InputStreamReader(fileInputStream));
        return new InputFileReader(in);
    }

    private FileInputStream openFileStream(String file) {
        FileInputStream f;
        try {
            f = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return f;
    }
}
