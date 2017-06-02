package com.yngvark.gridwalls.microservices.netcom_forwarder.app;

import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;

class NetworkMsgListenerFactory {
    public NetworkMsgListener create(OutputFileWriter microserviceWriter) {
        return new NetworkMsgListener(microserviceWriter);
    }
}
