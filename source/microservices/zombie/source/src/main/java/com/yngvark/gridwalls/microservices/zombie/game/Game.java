package com.yngvark.gridwalls.microservices.zombie.game;

import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import org.slf4j.Logger;

import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

public class Game {
    private final Logger logger = getLogger(getClass());
    private final OutputFileWriter outputFileWriter;
    private final GameLogic gameLogic;

    private boolean run = true;

    public Game(OutputFileWriter outputFileWriter, GameLogic gameLogic) {
        this.outputFileWriter = outputFileWriter;
        this.gameLogic = gameLogic;
    }

    public void produce() throws IOException, InterruptedException {
        outputFileWriter.write("/myNameIs netcomForwarderTest");

        while (run) {
//            String msg = "/publish Hey this is from Zombie, line " + i;
            String msg = gameLogic.nextMsg();
            outputFileWriter.write("/publish " + msg);
        }
        logger.info("Game done.");
    }

    public void stop() {
        logger.info("Stopping message generator.");
        run = false;
    }
}
