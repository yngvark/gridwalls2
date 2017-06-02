package com.yngvark.communicate_through_named_pipes.input;

import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

public class InputFileReader {
    private final Logger logger = getLogger(getClass());
    private BufferedReader in;

    private boolean run = true;

    public InputFileReader(BufferedReader in) {
        this.in = in;
    }

    public void consume(MessageListener messageListener) throws IOException {
        logger.info("Consume: start.");

        String msg;
        while ((msg = in.readLine()) != null && run) {
            logger.trace("<<< From other side: " + msg);
            messageListener.messageReceived(msg);
        }

        if (msg == null) {
            logger.info("Consume file stream was closed from other side.");
        }

        in.close();

        logger.info("");
        logger.info("Consume: done.");
    }

    public void closeStream() {
        logger.info("Stopping consuming input file...");
        run = false;

        if (in != null) {
            try {
                in.close();
                in = null;
            } catch (IOException e) {
                logger.info("Caught exception when stopping consuming.");
                e.printStackTrace();
            }
        }

        logger.info("Stopping consuming input file... done");
    }
}
