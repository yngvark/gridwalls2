package com.yngvark.gridwalls.microservices.netcom_forwarder.app.forward_msgs_to_microservice;

import com.yngvark.gridwalls.microservices.netcom_forwarder.app.forward_msgs_to_microservice.NetworkMsgListenerFactory;

public class NetworkMsgListenerFactoryFactory {
    public static NetworkMsgListenerFactory create() {
        return new NetworkMsgListenerFactory();
    }
}
