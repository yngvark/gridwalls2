package com.yngvark.gridwalls.microservices.netcom_forwarder.app;

import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.gridwalls.rabbitmq.RabbitMessageListener;
import org.slf4j.Logger;

import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

class NetworkMsgListener implements RabbitMessageListener {
    private final Logger logger = getLogger(getClass());
    private final OutputFileWriter microserviceWriter;
    private final String exchange;

    public NetworkMsgListener(OutputFileWriter microserviceWriter, String exchange) {
        this.microserviceWriter = microserviceWriter;
        this.exchange = exchange;
    }

    @Override
    public void messageReceived(String msgFromNetwork) {
        logger.info("From network: " + msgFromNetwork);
        String msgToMs = "[" + exchange + "] " + msgFromNetwork;
        microserviceWriter.write(msgToMs);
    }
}
