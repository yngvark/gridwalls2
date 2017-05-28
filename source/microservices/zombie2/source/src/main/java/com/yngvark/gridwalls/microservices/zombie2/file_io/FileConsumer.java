package com.yngvark.gridwalls.microservices.zombie2.file_io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileConsumer {
    private final String fifoInputFilename;

    private boolean run = true;
    private BufferedReader in;

    public FileConsumer(String fifoInputFilename) {
        this.fifoInputFilename = fifoInputFilename;
    }

    public void consume() throws IOException {
        System.out.println("Opening consume file... " + fifoInputFilename);
        FileInputStream fileInputStream = new FileInputStream(fifoInputFilename);
        System.out.println("Opening consume file... done.");

        in = new BufferedReader(new InputStreamReader(fileInputStream));

        System.out.println("Consume: start.");

        String read;
        while ((read = in.readLine()) != null && run) {
            System.out.println("<<< From netcom: " + read);
        }

        if (read == null) {
            System.out.println("Consume file stream was closed from other side.");
        }

        in.close();

        System.out.println("");
        System.out.println("Consume: done.");
    }

    public void stopConsuming() {
        System.out.println("Stopping consuming input file...");
        run = false;

        if (in != null) {
            try {
                in.close();
                in = null;
            } catch (IOException e) {
                System.out.println("Caught exception when stopping consuming.");
                e.printStackTrace();
            }
        }

        System.out.println("Stopping consuming input file... done");
    }
}
