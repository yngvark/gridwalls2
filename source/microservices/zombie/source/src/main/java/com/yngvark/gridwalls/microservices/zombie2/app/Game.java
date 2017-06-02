package com.yngvark.gridwalls.microservices.zombie2.app;

import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import org.slf4j.Logger;

import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

class Game {
    private final Logger logger = getLogger(getClass());
    private final OutputFileWriter outputFileWriter;

    private boolean run = true;

    public Game(OutputFileWriter outputFileWriter) {
        this.outputFileWriter = outputFileWriter;
    }

    public void produce() throws IOException, InterruptedException {
        for (int i = 0; i < 1000 && run; i++) {
            String msg = "Hey this is from Zombie, line " + i;
            outputFileWriter.write(msg);
            Thread.sleep(1000);
        }
        logger.info("Game loging done.");
    }

    public void stop() {
        logger.info("Stopping message generator.");
        run = false;
    }
}
