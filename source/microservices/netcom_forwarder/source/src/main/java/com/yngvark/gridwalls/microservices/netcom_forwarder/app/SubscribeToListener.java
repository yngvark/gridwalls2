package com.yngvark.gridwalls.microservices.netcom_forwarder.app;

import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.gridwalls.microservices.netcom_forwarder.app.consume_input_file.FileMessageListener;
import com.yngvark.gridwalls.rabbitmq.RabbitConsumer;
import com.yngvark.gridwalls.rabbitmq.RabbitMessageListener;
import com.yngvark.gridwalls.rabbitmq.RabbitSubscriber;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.slf4j.LoggerFactory.getLogger;

class SubscribeToListener implements FileMessageListener {
    private final Logger logger = getLogger(getClass());
    private final BlockingQueue blockingQueue = new LinkedBlockingQueue();
    private List<RabbitConsumer> consumers = new ArrayList<>();


    private final String consumerName;
    private final RabbitMessageListenerFactory rabbitMessageListenerFactory;
    private final OutputFileWriter outputFileWriter;
    private final RabbitSubscriber rabbitSubscriber;

    private boolean isStopped = false;

    public SubscribeToListener(String consumerName,
            RabbitMessageListenerFactory rabbitMessageListenerFactory,
            OutputFileWriter outputFileWriter,
            RabbitSubscriber rabbitSubscriber) {
        this.consumerName = consumerName;
        this.rabbitMessageListenerFactory = rabbitMessageListenerFactory;
        this.outputFileWriter = outputFileWriter;
        this.rabbitSubscriber = rabbitSubscriber;
    }

    public void blockUntilStopped()
            throws InterruptedException {
        logger.info("Blocking...");
        blockingQueue.take();
        logger.info("Blocking done.");
    }

    @Override
    public void messageReceived(String exchange) {
        logger.info("Subscribing consumer '{}' to exchange '{}'", consumerName, exchange);
        RabbitMessageListener rabbitMessageListener = rabbitMessageListenerFactory.create(outputFileWriter, exchange);
        RabbitConsumer rabbitConsumer = rabbitSubscriber.subscribe(consumerName, exchange, rabbitMessageListener);
        consumers.add(rabbitConsumer);
        logger.info("Consumer '{}' subscribed to exchange '{}'", consumerName, exchange);
    }

    public synchronized void stop() {
        logger.info("Stopping...");

        if (isStopped) {
            logger.info("Already stopped.");
            return;
        }

        for (RabbitConsumer consumer : consumers) {
            try {
                consumer.stop();
            } catch (IOException e) {
                logger.error("Error when stopping consumer: {}. Exception: {}", consumer, e);
            }
        }

        isStopped = true;
        blockingQueue.add("You can stop consuming now.");

        logger.info("Stopping... done.");
    }
}
