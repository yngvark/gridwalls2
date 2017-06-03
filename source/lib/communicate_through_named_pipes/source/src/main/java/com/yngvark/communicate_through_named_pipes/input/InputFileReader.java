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
        logger.info("Consume: start.");

        String msg;
        while ((msg = reader.readLine()) != null && run) {
            logger.trace("<<< From other side: " + msg);
            messageListener.messageReceived(msg);
        }

        if (msg == null) {
            logger.info("Consume file stream was closed from other side.");
        }

        reader.close();

        logger.info("");
        logger.info("Consume: done.");
    }

    public void closeStream() {
        logger.info("Stopping consuming input file...");
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

        logger.info("Stopping consuming input file... done");
    }
}
