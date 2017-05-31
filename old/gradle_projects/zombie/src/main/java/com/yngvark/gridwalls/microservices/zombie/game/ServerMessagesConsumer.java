package com.yngvark.gridwalls.microservices.zombie.game;

import com.yngvark.gridwalls.netcom.Netcom;

public class ServerMessagesConsumer {
    private final Netcom netcom;
    private final ShutdownFromServerHandler shutdownFromServerHandler;

    public ServerMessagesConsumer(Netcom netcom, ShutdownFromServerHandler shutdownFromServerHandler) {
        this.netcom = netcom;
        this.shutdownFromServerHandler = shutdownFromServerHandler;
    }

    public void startConsumingEvents() {
        netcom.startConsume("server_messages", shutdownFromServerHandler);
    }
}
