package com.yngvark.communicate_through_named_pipes.input;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;

public class InputFileLineReader {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final BufferedReader bufferedReader;

    private boolean run = true;
    private boolean streamClosed = false;

    public InputFileLineReader(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    /**
     * @throws IORuntimeException If an {@link java.io.IOException} occurs.
     */
    public String readLine() throws IORuntimeException {
        try {
            return bufferedReader.readLine();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public synchronized void closeStream() {
        logger.debug("Stopping consuming input file...");
        if (streamClosed) {
            logger.warn("Already stopped.");
            return;
        }

        run = false;
        try {
            bufferedReader.close();
            streamClosed = true;
        } catch (IOException e) {
            logger.error("Caught exception when closing stream: {}", e);
        }

        logger.debug("Stopping consuming input file... done");
    }
}
