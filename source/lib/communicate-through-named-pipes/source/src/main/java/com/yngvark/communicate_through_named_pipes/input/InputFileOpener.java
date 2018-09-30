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

    /**
     * @throws FileNotFoundRuntimeException If a {@link FileNotFoundException} occurs.
     */
    public InputFileReader openStream(RetrySleeper retrySleeper) throws FileNotFoundRuntimeException {
        return new InputFileReader(createReader(retrySleeper));
    }

    private FileInputStream openFileStream(String file) {
        FileInputStream f;
        try {
            f = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundRuntimeException(e);
        }
        return f;
    }

    public InputFileLineReader openLineStream(RetrySleeper retrySleeper) {
        return new InputFileLineReader(createReader(retrySleeper));
    }

    public BufferedReader createReader(RetrySleeper retrySleeper) {
        logger.info("Opening input file... " + fifoInputFilename);

        RetryWaiter retryWaiter = new RetryWaiter(retrySleeper);
        retryWaiter.waitUntilFileExists(fifoInputFilename);

        logger.debug("File exists. Opening stream.");
        FileInputStream fileInputStream = openFileStream(fifoInputFilename);
        logger.info("Opening input file... done.");

        return new BufferedReader(new InputStreamReader(fileInputStream));
    }
}
