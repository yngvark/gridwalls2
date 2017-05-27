package com.yngvark.gridwalls.microservices.netcom_forwarder.file_io;

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
        System.out.println("Consume file opened.");

        in = new BufferedReader(new InputStreamReader(fileInputStream));

        String read;
        while ((read = in.readLine()) != null && run) {
            System.out.println("<<< From netcom: " + read);
        }

        in.close();
    }

    public void stopConsuming() {
        System.out.println("Stopping consuming input file.");
        run = false;

        if (in != null) {
            try {
                in.close();
                System.out.println("Stopped consuming input file.");

                in = null;
            } catch (IOException e) {
                System.out.println("Caught exception when stopping consuming.");
                e.printStackTrace();
            }
        }

    }
}
