package com.yngvark.communicate_through_named_pipes.output;

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

    public OutputFileWriter openStream() throws FileNotFoundException {
        logger.info("Opening output file... " + fifoOutputFilename);
        FileOutputStream fileOutputStream = new FileOutputStream(fifoOutputFilename);
        logger.info("Opening output file... done.");

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

        return new OutputFileWriter(out);
    }

}
