package com.yngvark.communicate_through_named_pipes.output;

import com.yngvark.communicate_through_named_pipes.RetrySleeper;
import com.yngvark.communicate_through_named_pipes.RetryWaiter;
import org.slf4j.Logger;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import static org.slf4j.LoggerFactory.getLogger;

public class OutputFileOpener {
    private final Logger logger = getLogger(getClass());
    private final String fifoOutputFilename;

    public OutputFileOpener(String fifoOutputFilename) {
        this.fifoOutputFilename = fifoOutputFilename;
    }

    public OutputFileWriter openStream(RetrySleeper retrySleeper) {
        logger.info("Opening output file... " + fifoOutputFilename);

        RetryWaiter retryWaiter = new RetryWaiter(retrySleeper);
        retryWaiter.waitUntilFileExists(fifoOutputFilename);
        FileOutputStream fileOutputStream = openFileStream(fifoOutputFilename);

        logger.info("Opening output file... done.");

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
        return new OutputFileWriter(out);
    }

    private FileOutputStream openFileStream(String file) {
        FileOutputStream f;
        try {
            f = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return f;
    }

}
