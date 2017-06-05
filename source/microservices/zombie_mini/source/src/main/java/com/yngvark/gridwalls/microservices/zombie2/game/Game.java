package com.yngvark.gridwalls.microservices.zombie2.game;

import com.yngvark.communicate_through_named_pipes.input.MessageListener;
import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import org.slf4j.Logger;

import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

public class Game implements MessageListener {
    private final Logger logger = getLogger(getClass());
    private final OutputFileWriter outputFileWriter;

    private boolean run = true;

    public Game(OutputFileWriter outputFileWriter) {
        this.outputFileWriter = outputFileWriter;
    }

    public void init() {
        try {
            outputFileWriter.write("/myNameIs zombie");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void produce() throws IOException, InterruptedException {

        for (int i = 0; i < 1000 && run; i++) {
            String msg = "/publish Hey this is from Zombie, line " + i;
            outputFileWriter.write(msg);
            Thread.sleep(1000);
        }
        logger.info("Game loging done.");
    }

    public void stop() {
        logger.info("Stopping message generator.");
        run = false;
    }

    @Override
    public void messageReceived(String s) {

    }
}
