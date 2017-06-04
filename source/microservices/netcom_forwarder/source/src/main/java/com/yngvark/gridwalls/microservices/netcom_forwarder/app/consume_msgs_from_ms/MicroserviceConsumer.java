package com.yngvark.gridwalls.microservices.netcom_forwarder.app.consume_msgs_from_ms;

import com.yngvark.communicate_through_named_pipes.input.InputFileReader;
import com.yngvark.gridwalls.microservices.netcom_forwarder.app.consume_msgs_from_network.Netcom;
import com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq.RabbitConnection;
import com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq.RabbitPublisher;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

public class MicroserviceConsumer {
    private final Logger logger = getLogger(getClass());
    private final RabbitConnection rabbitConnection;
    private final RabbitPublisherFactory rabbitPublisherFactory;
    private final InputFileReader microserviceReader;
    private final Netcom netcom;

    public static MicroserviceConsumer create(
            RabbitConnection rabbitConnection,
            InputFileReader microserviceReader,
            Netcom netcom) {
        return new MicroserviceConsumer(
                rabbitConnection,
                new RabbitPublisherFactory(),
                microserviceReader,
                netcom
        );
    }

    public MicroserviceConsumer(
            RabbitConnection rabbitConnection,
            RabbitPublisherFactory rabbitPublisherFactory,
            InputFileReader microserviceReader,
            Netcom netcom) { // TODO clean up design.
        this.rabbitConnection = rabbitConnection;
        this.rabbitPublisherFactory = rabbitPublisherFactory;
        this.microserviceReader = microserviceReader;
        this.netcom = netcom;
    }

    public void consume() throws IOException {
        RabbitPublisher rabbitPublisher = rabbitPublisherFactory.create(rabbitConnection);

        ConsumerName consumerName = new ConsumerName();
        microserviceReader.consume((msg) -> {
            logger.info("From microservice: " + msg);

            // By convention, first message should be the name of consumer
            if (consumerName.isEmpty()) {
                logger.info("Setting consumer name to: {}", msg);
                consumerName.set(msg);
                return;
            }

            if (msg.startsWith("/subscribe")) {
                String exchange = parseExchange(msg);
                logger.info("Subscribing to exchange: {}", exchange);
                netcom.subscribeToExchange(consumerName.get(), exchange);
                return;
            }

            rabbitPublisher.publish(consumerName.get(), msg);
        });
    }

    private String parseExchange(String msg) {
        String[] parts = StringUtils.split(msg, " ", 2);
        if (parts.length != 2)
            logger.error("Cannot make subscription. Usage: /subscribe <exchange (no spaces)>");
        return parts[1];
    }


}
