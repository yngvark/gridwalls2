package com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq;

import org.slf4j.Logger;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.slf4j.LoggerFactory.getLogger;

public class BlockingRabbitConsumer {
    private final Logger logger = getLogger(getClass());
    private final RabbitConsumer rabbitConsumer;

    private boolean isConsuming = false;
    private RabbitConsumerData rabbitConsumerData;
    private BlockingQueue blockingQueue;


    public static BlockingRabbitConsumer create() {
        return new BlockingRabbitConsumer(new RabbitConsumer());
    }

    public BlockingRabbitConsumer(RabbitConsumer rabbitConsumer) {
        this.rabbitConsumer = rabbitConsumer;
    }

    public void consume(
            RabbitConnection rabbitConnection, String queueName, RabbitMessageListener rabbitMessageListener) {
        if (isConsuming)
            throw new IllegalStateException("Already consuming");
        isConsuming = true;

        blockingQueue = new LinkedBlockingQueue();
        rabbitConsumerData = rabbitConsumer.startConsume(rabbitConnection, queueName, rabbitMessageListener);

        try {
            blockingQueue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interruped while waiting for consume to stop", e);
        }
    }

    public void stop() {
        try {
            logger.info("Stopping {}... basicCancel tag: {}",
                    getClass().getSimpleName(), rabbitConsumerData.getConsumerTag());
            rabbitConsumerData.getChannel().basicCancel(rabbitConsumerData.getConsumerTag());
            blockingQueue.add("You can stop consuming now.");

        } catch (IOException e) {
            throw new RuntimeException("Could not stop " + getClass().getSimpleName(), e);
        }

        logger.info("Stopping {}... done", getClass().getSimpleName());
    }
}
