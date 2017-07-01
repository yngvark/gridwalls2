package com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs;

import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.gridwalls.microservices.zombie.run_game.GameEventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class BlockingGameEventProducer implements GameEventProducer {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final OutputFileWriter outputFileWriter;
    private final ProducerContext producerContext;

    private boolean run = true;

    public BlockingGameEventProducer(OutputFileWriter outputFileWriter,
            ProducerContext producerContext) {
        this.outputFileWriter = outputFileWriter;
        this.producerContext = producerContext;
    }

    public void produce() {
        try {
            tryToProduce();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void tryToProduce() throws IOException {
        while (run) {
            String msg = produceNext();
            outputFileWriter.write(msg);
        }
        logger.info("Game done.");
    }

    public String produceNext() {
        String msg = producerContext.nextMsg();
        logger.info(">>> {}", msg);
        return msg;
    }

    public void stop() {
        logger.info("Stopping message generator.");
        run = false;
    }
}
