package com.yngvark.gridwalls.microservices.netcom_forwarder.app;

import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq.RabbitMessageListener;

class RabbitMessageListenerFactory {
    public RabbitMessageListener create(OutputFileWriter microserviceWriter, String exchange) {
        return new NetworkMsgListener(microserviceWriter, exchange);
    }
}
