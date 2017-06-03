package com.yngvark.gridwalls.microservices.netcom_forwarder.app.forward_msgs_to_microservice;

import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq.RabbitMessageListener;
import org.slf4j.Logger;

import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

class NetworkMsgListener implements RabbitMessageListener {
    private final Logger logger = getLogger(getClass());
    private final OutputFileWriter microserviceWriter;

    public NetworkMsgListener(OutputFileWriter microserviceWriter) {
        this.microserviceWriter = microserviceWriter;
    }

    @Override
    public void messageReceived(String msg) {
        logger.info("From network: " + msg);
        try {
            microserviceWriter.write(msg);
        } catch (IOException e) {
            throw new RuntimeException("Error when forwarding message to microservice.", e);
        }
    }
}
