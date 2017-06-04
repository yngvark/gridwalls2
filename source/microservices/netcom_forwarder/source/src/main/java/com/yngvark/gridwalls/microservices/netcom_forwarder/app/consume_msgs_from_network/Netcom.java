package com.yngvark.gridwalls.microservices.netcom_forwarder.app.consume_msgs_from_network;

import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq.RabbitConnection;
import com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq.RabbitConsumer;
import com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq.RabbitSubscribe;
import com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq.RabbitMessageListener;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.slf4j.LoggerFactory.getLogger;

public class Netcom {
    private final Logger logger = getLogger(getClass());

    private final NetworkMsgListenerFactory networkMsgListenerFactory;
    private final RabbitConnection rabbitConnection;
    private final OutputFileWriter microserviceWriter;
    private final RabbitSubscribe rabbitSubscribe;

    private final BlockingQueue blockingQueue = new LinkedBlockingQueue();

    private List<RabbitConsumer> consumers = new ArrayList<>();

    private boolean isStopped = false;

    public static Netcom create(
            RabbitConnection rabbitConnection,
            OutputFileWriter microserviceWriter) {
        return new Netcom(
                new NetworkMsgListenerFactory(),
                rabbitConnection,
                microserviceWriter,
                new RabbitSubscribe(rabbitConnection));
    }

    public Netcom(
            NetworkMsgListenerFactory networkMsgListenerFactory,
            RabbitConnection rabbitConnection,
            OutputFileWriter microserviceWriter,
            RabbitSubscribe rabbitSubscribe) {
        this.networkMsgListenerFactory = networkMsgListenerFactory;
        this.rabbitConnection = rabbitConnection;
        this.microserviceWriter = microserviceWriter;
        this.rabbitSubscribe = rabbitSubscribe;
    }

    public void blockUntilStopped()
            throws InterruptedException {
        logger.info("Blocking...");
        blockingQueue.take();
        logger.info("Blocking done.");
    }

    public void subscribeToExchange(String consumerName, String exchange) {
        RabbitMessageListener rabbitMessageListener = networkMsgListenerFactory.create(microserviceWriter, exchange);
        RabbitConsumer rabbitConsumer = rabbitSubscribe.subscribe(
                consumerName, exchange, rabbitMessageListener);
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
