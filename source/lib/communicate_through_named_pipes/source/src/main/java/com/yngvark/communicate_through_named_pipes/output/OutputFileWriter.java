package com.yngvark.communicate_through_named_pipes.output;

import org.slf4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

public class OutputFileWriter {
    private final Logger logger = getLogger(getClass());
    private final BufferedWriter writer;

    private boolean streamClosed = false;

    public OutputFileWriter(BufferedWriter writer) {
        this.writer = writer;
    }

    /**
     * Writes the message to the output.
     *
     * @param msg The message to write.
     * @throws RuntimeException if an {@link IOException} occurs.
     */
    public void write(String msg) throws RuntimeException {
        try {
            writeRaw(msg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeRaw(String msg) throws IOException {
        logger.info(">>> Sending: " + msg);

        writer.write(msg);
        writer.newLine();
        writer.flush();
    }

    public void closeStream() {
        logger.info("Closing output file...");
        if (streamClosed) {
            logger.warn("Already stopped.");
            return;
        }

        try {
            writer.close();
            streamClosed = true;
        } catch (IOException e) {
            logger.error("Caught exception when closing stream: {}", e);
        }

        logger.info("Closing output file... done.");
    }
}
