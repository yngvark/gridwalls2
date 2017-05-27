package com.yngvark.gridwalls.microservices.zombie2.app;

import com.yngvark.gridwalls.microservices.zombie2.netcom.NetcomSender;

import java.io.IOException;

class Game {
    private final NetcomSender netcomSender;

    private boolean run = true;

    public Game(NetcomSender netcomSender) {
        this.netcomSender = netcomSender;
    }

    public void produce() throws IOException, InterruptedException {
        for (int i = 0; i < 4 && run; i++) {
            String msg = "Hey this is from Zombie, line " + i;
            netcomSender.send(msg);
            Thread.sleep(1000);
        }
    }

    public void stop() {
        System.out.println("Stopping message generator.");
        run = false;
    }
}
