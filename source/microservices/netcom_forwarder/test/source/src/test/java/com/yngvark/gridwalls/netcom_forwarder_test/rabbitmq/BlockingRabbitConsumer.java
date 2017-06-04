package com.yngvark.gridwalls.netcom_forwarder_test.rabbitmq;

import org.slf4j.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.slf4j.LoggerFactory.getLogger;

public class BlockingRabbitConsumer {
    private final Logger logger = getLogger(getClass());
    private final RabbitSubscriber rabbitSubscriber;

    private boolean isConsuming = false;
    private RabbitConsumer rabbitConsumer;
    private BlockingQueue blockingQueue;
    private boolean isStopped = false;

//    public static BlockingRabbitConsumer create() {
//        return new BlockingRabbitConsumer(new RabbitConsumerStarter());
//    }

    public BlockingRabbitConsumer(RabbitSubscriber rabbitSubscriber) {
        this.rabbitSubscriber = rabbitSubscriber;
    }

    public void consume(
            RabbitConnection rabbitConnection, String exchange, RabbitMessageListener rabbitMessageListener) {
        if (isConsuming)
            throw new IllegalStateException("Already consuming");
        isConsuming = true;

        blockingQueue = new LinkedBlockingQueue();
//        rabbitConsumer = rabbitConsumerStarter.subscribe(rabbitConnection, exchange, rabbitMessageListener);

        try {
            blockingQueue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interruped while waiting for consume to stop", e);
        }
    }

    public synchronized void stop() {
//        logger.info("Stopping {}... basicCancel tag: {}",
//                getClass().getSimpleName(), rabbitConsumer.getConsumerTag());

        if (isStopped) {
            logger.info("Already stopped.");
            return;
        }
        isStopped = true;

//        try {
//            rabbitConsumer.getChannel().basicCancel(rabbitConsumer.getConsumerTag());
//            blockingQueue.add("You can stop consuming now.");
//
//        } catch (IOException e) {
//            throw new RuntimeException("Could not stop " + getClass().getSimpleName(), e);
//        }

        logger.info("Stopping... done");
    }
}
