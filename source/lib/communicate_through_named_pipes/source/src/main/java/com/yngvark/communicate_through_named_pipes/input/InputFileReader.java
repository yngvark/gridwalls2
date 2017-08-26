package com.yngvark.communicate_through_named_pipes.input;

import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

public class InputFileReader {
    private final Logger logger = getLogger(getClass());
    private final BufferedReader bufferedReader;

    private boolean run = true;
    private boolean streamClosed = false;

    public InputFileReader(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    /**
     * @throws IORuntimeException If an {@link java.io.IOException} occurs.
     */
    public void consume(MessageListener messageListener) throws RuntimeException {
        logger.debug("Consume: start.");

        try {
            tryToConsume(messageListener);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }

        logger.debug("");
        logger.debug("Consume: done.");
    }

    private void tryToConsume(MessageListener messageListener) throws IOException {
        String msg = null;
        while (run) {
            msg = bufferedReader.readLine();
            if (msg == null)
                break;

            logger.trace("<<< From other side: " + msg);
            messageListener.messageReceived(msg);
        }

        if (msg == null) {
            logger.debug("Consume file stream was closed from other side.");
        }

        bufferedReader.close();
    }

    public synchronized void closeStream() {
        logger.debug("Stopping consuming input file...");
        if (streamClosed) {
            logger.info("Already stopped.");
            return;
        }

        run = false;
        try {
            logger.trace("Closing buffered reader.");
            bufferedReader.close();
            streamClosed = true;
        } catch (IOException e) {
            logger.error("Caught exception when closing stream: {}", e);
        }

        logger.debug("Stopping consuming input file... done");
    }
}
