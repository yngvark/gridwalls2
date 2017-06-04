package com.yngvark.gridwalls.microservices.netcom_forwarder.app;

import com.yngvark.gridwalls.microservices.netcom_forwarder.app.consume_input_file.FileMessageListener;
import com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq.RabbitPublisher;

class PublishListener implements FileMessageListener {
    private final RabbitPublisher rabbitPublisher;
    private final ConsumerNameListener consumerNameListener;

    public PublishListener(RabbitPublisher rabbitPublisher,
            ConsumerNameListener consumerNameListener) {
        this.rabbitPublisher = rabbitPublisher;
        this.consumerNameListener = consumerNameListener;
    }

    @Override
    public void messageReceived(String msg) {
        rabbitPublisher.publish(consumerNameListener.getConsumerName(), msg);
    }
}
