package com.yngvark.communicate_through_named_pipes.write;

import org.slf4j.Logger;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import static org.slf4j.LoggerFactory.getLogger;

public class FileOpener {
    private final Logger logger = getLogger(getClass());
    private final String fifoOutputFilename;

    public FileOpener(String fifoOutputFilename) {
        this.fifoOutputFilename = fifoOutputFilename;
    }

    public FileWriter openStream() throws FileNotFoundException {
        logger.info("Opening output file... " + fifoOutputFilename);
        FileOutputStream fileOutputStream = new FileOutputStream(fifoOutputFilename);
        logger.info("Opening output file... done.");

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

        return new FileWriter(out);
    }

}
