package com.yngvark.gridwalls.microservices.netcom_forwarder.app;

import com.yngvark.communicate_through_named_pipes.file_io.FileWriter;

import java.io.IOException;

class NetworkToFileHub {
    private final FileWriter fileWriter;

    private boolean run = true;

    public NetworkToFileHub(FileWriter fileWriter) {
        this.fileWriter = fileWriter;
    }

    public void consumeAndForward() throws IOException, InterruptedException {
        for (int i = 0; i < 10 && run; i++) {
            String msg = "NETWORK UHU This is from Weather, line " + i; // Should come from rabbitMq
            fileWriter.write(msg);
            Thread.sleep(1000);
        }
    }

    public void stop() {
        System.out.println("Stopping " + getClass().getSimpleName());
        run = false;
    }
}
