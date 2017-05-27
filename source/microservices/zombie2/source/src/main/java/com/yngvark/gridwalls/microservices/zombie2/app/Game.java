package com.yngvark.gridwalls.microservices.zombie2.app;

import com.yngvark.gridwalls.microservices.zombie2.file_io.FileWriter;

import java.io.IOException;

class Game {
    private final FileWriter fileWriter;

    private boolean run = true;

    public Game(FileWriter fileWriter) {
        this.fileWriter = fileWriter;
    }

    public void produce() throws IOException, InterruptedException {
        for (int i = 0; i < 4 && run; i++) {
            String msg = "Hey this is from Zombie, line " + i;
            fileWriter.write(msg);
            Thread.sleep(1000);
        }

        //fileWriter.write("/quit");
    }

    public void stop() {
        System.out.println("Stopping message generator.");
        run = false;
    }
}
