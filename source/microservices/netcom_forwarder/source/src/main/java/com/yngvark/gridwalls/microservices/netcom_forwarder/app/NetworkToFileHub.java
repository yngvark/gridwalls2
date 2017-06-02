package com.yngvark.gridwalls.microservices.netcom_forwarder.app;

import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq.RabbitBrokerConnecter;
import com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq.RabbitConnection;
import com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq.RabbitConsumer;
import org.slf4j.Logger;

import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

class NetworkToFileHub {
    private final Logger logger = getLogger(getClass());
    private final RabbitBrokerConnecter rabbitBrokerConnecter;
    private final NetworkMsgListenerFactory networkMsgListenerFactory;

    public NetworkToFileHub(
            RabbitBrokerConnecter rabbitBrokerConnecter,
            NetworkMsgListenerFactory networkMsgListenerFactory) {
        this.rabbitBrokerConnecter = rabbitBrokerConnecter;
        this.networkMsgListenerFactory = networkMsgListenerFactory;
    }

    public void consumeAndForwardTo(OutputFileWriter microserviceWriter) throws IOException, InterruptedException {
        RabbitConnection rabbitConnection = rabbitBrokerConnecter.connect("rabbithost");
        RabbitConsumer rabbitConsumer = new RabbitConsumer(rabbitConnection);
        rabbitConsumer.consume("game", networkMsgListenerFactory.create(microserviceWriter));
    }

    public void stop() {
        logger.info("Stopping " + getClass().getSimpleName());
        throw IOException("TODO Abort rabbitconsumer somehow");
    }

//    private final Logger logger = getLogger(getClass());
//    private final RabbitBrokerConnecter rabbitBrokerConnecter;
//
//    public NetworkToFileHub(RabbitBrokerConnecter rabbitBrokerConnecter) {
//        this.rabbitBrokerConnecter = rabbitBrokerConnecter;
//    }
//
//    public void consumeAndForward(NetworkMsgListener networkMsgListener) throws IOException, InterruptedException {
//        RabbitConnection rabbitConnection = rabbitBrokerConnecter.connect("rabbithost");
//        RabbitConsumer rabbitConsumer = new RabbitConsumer(rabbitConnection);
//        rabbitConsumer.consume("game", networkMsgListener);
//    }
//
//    public void stop() {
//        logger.info("Stopping " + getClass().getSimpleName());
//        throw IOException("TODO Abort rabbitconsumer somehow");
//    }
}
