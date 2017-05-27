package com.yngvark.gridwalls.microservices.netcom_forwarder.exit_os_process;

import com.yngvark.gridwalls.microservices.netcom_forwarder.app.App;

public class Shutdownhook {
    private final App app;

    public Shutdownhook(App app) {
        this.app = app;
    }

    public void run() {
        System.out.println("Running shutdownhook");
        app.stop();
    }
}
