package com.yngvark.gridwalls.microservices.netcom_forwarder.app;

import com.yngvark.gridwalls.microservices.netcom_forwarder.app.consume_input_file.FileMessageListener;
import com.yngvark.gridwalls.rabbitmq.RabbitPublisher;
import org.apache.commons.lang3.StringUtils;

class PublishListener implements FileMessageListener {
    private final RabbitPublisher rabbitPublisher;

    public PublishListener(RabbitPublisher rabbitPublisher) {
        this.rabbitPublisher = rabbitPublisher;
    }

    @Override
    public void messageReceived(String publishCommand) {
        String[] parts = StringUtils.split(publishCommand, " ", 2);
        String exchange = parts[0];
        String msg = parts[1];
        rabbitPublisher.publish(exchange, msg);
    }
}
