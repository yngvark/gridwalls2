package com.yngvark.gridwalls.microservices.netcom_forwarder.app.forward_msgs;

public class NetworkMsgListenerFactoryFactory {
    public static NetworkMsgListenerFactory create() {
        return new NetworkMsgListenerFactory();
    }
}
