package com.yngvark.gridwalls.microservices.netcom_forwarder.app.consume_msgs_from_network;

public class NetworkMsgListenerFactoryFactory {
    public static NetworkMsgListenerFactory create() {
        return new NetworkMsgListenerFactory();
    }
}
