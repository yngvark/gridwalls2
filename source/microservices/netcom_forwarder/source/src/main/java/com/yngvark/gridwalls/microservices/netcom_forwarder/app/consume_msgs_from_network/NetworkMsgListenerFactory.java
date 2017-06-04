package com.yngvark.gridwalls.microservices.netcom_forwarder.app.consume_msgs_from_network;

import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;

class NetworkMsgListenerFactory {
    public NetworkMsgListener create(OutputFileWriter microserviceWriter, String exchange) {
        return new NetworkMsgListener(microserviceWriter, exchange);
    }
}
