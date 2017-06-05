package com.yngvark.communicate_through_named_pipes.input;

import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

public class InputFileReader {
    private final Logger logger = getLogger(getClass());
    private final BufferedReader reader;

    private boolean run = true;
    private boolean streamClosed = false;

    public InputFileReader(BufferedReader reader) {
        this.reader = reader;
    }

    public void consume(MessageListener messageListener) throws IOException {
        logger.debug("Consume: start.");

        String msg = null;
        while (run) {
            msg = reader.readLine();
            if (msg == null)
                break;

            logger.trace("<<< From other side: " + msg);
            messageListener.messageReceived(msg);
        }

        if (msg == null) {
            logger.debug("Consume file stream was closed from other side.");
        }

        reader.close();

        logger.debug("");
        logger.debug("Consume: done.");
    }

    public void closeStream() {
        logger.debug("Stopping consuming input file...");
        if (streamClosed) {
            logger.warn("Already stopped.");
            return;
        }

        run = false;

        try {
            reader.close();
            streamClosed = true;
        } catch (IOException e) {
            logger.error("Caught exception when closing stream: {}", e);
        }

        logger.debug("Stopping consuming input file... done");
    }
}
