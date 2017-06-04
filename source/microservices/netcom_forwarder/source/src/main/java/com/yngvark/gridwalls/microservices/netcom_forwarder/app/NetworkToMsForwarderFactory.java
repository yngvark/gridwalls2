package com.yngvark.gridwalls.microservices.netcom_forwarder.app;

import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.gridwalls.microservices.netcom_forwarder.app.consume_msgs_from_network.Netcom;
import com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq.RabbitConnection;

class NetworkToMsForwarderFactory {
    public Netcom create(RabbitConnection rabbitConnection, OutputFileWriter microserviceWriter) {
        return Netcom.create(rabbitConnection, microserviceWriter);
    }
}
