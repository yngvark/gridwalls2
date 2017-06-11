package com.yngvark.gridwalls.microservices.zombie.game;

import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.gridwalls.microservices.zombie.app.GameEventProducer;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import static org.slf4j.LoggerFactory.getLogger;

class BlockingGameEventProducer implements GameEventProducer {
    private final Logger logger = getLogger(getClass());
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
            produceOne();
        }
        logger.info("Game done.");
    }

    private void produceOne() throws IOException {
        String msg = nextMsg();
        outputFileWriter.write(msg);
    }

    String nextMsg() {
        String msg = producerContext.nextMsg();
        logger.info(">>> {}", msg);
        return msg;
    }

    public void stop() {
        logger.info("Stopping message generator.");
        run = false;
    }
}
