package com.yngvark.communicate_through_named_pipes.output;

import org.slf4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

public class OutputFileWriter {
    Logger logger = getLogger(getClass());

    private final BufferedWriter out;

    public OutputFileWriter(BufferedWriter out) {
        this.out = out;
    }

    public void write(String msg) throws IOException {
        msg = "[msg] " + msg;
        writeRaw(msg);
    }

    private void writeRaw(String msg) throws IOException {
        logger.info(">>> Sending: " + msg);

        out.write(msg);
        out.newLine();
        out.flush();
    }

    public void closeStream() throws IOException {
        logger.info("Closing output file...");
        out.close();
        logger.info("Closing output file... done.");
    }
}
