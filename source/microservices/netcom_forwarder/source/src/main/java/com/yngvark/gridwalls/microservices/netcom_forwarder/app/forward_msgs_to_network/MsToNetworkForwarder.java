package com.yngvark.gridwalls.microservices.netcom_forwarder.app.forward_msgs_to_network;

import com.yngvark.communicate_through_named_pipes.input.InputFileReader;
import com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq.RabbitConnection;
import com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq.RabbitPublisher;
import org.slf4j.Logger;

import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

public class MsToNetworkForwarder {
    private final Logger logger = getLogger(getClass());
    private final RabbitPublisherFactory rabbitPublisherFactory;
    private final String queueName;

    public static MsToNetworkForwarder create(String queueName) {
        return new MsToNetworkForwarder(
                new RabbitPublisherFactory(),
                queueName
        );
    }

    public MsToNetworkForwarder(RabbitPublisherFactory rabbitPublisherFactory, String queueName) {
        this.rabbitPublisherFactory = rabbitPublisherFactory;
        this.queueName = queueName;
    }

    public void consume(RabbitConnection rabbitConnection, InputFileReader microserviceReader) throws IOException {
        RabbitPublisher rabbitPublisher = rabbitPublisherFactory.create(rabbitConnection);
        microserviceReader.consume((msg) -> {
            logger.info("From microservice: " + msg);
            rabbitPublisher.publish(queueName, msg);
        });
    }
}
