package com.yngvark.gridwalls.microservices.zombie.game;

import com.yngvark.gridwalls.microservices.zombie.game.os_process.ShutdownHook;
import com.yngvark.gridwalls.netcom.Netcom;
import com.yngvark.gridwalls.netcom.consume.ConsumeHandler;

public class ShutdownFromServerHandler implements ConsumeHandler {
    private final ShutdownHook shutdownHook;

    public ShutdownFromServerHandler(ShutdownHook shutdownHook) {
        this.shutdownHook = shutdownHook;
    }

    @Override
    public void handleMessage(String msg) {
        System.out.println("--> Message from server: " + msg);
        if (msg.equals("shutdown")) {
            System.out.println("Shutdownhook called from server.");
            shutdownHook.shutdown();
        }
    }
}
