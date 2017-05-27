package com.yngvark.gridwalls.microservices.zombie2.netcom;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class NetcomSender {
    private final String fifoOutputFilename;
    private BufferedWriter out;

    public NetcomSender(String fifoOutputFilename) {
        this.fifoOutputFilename = fifoOutputFilename;
    }

    public void openStream() throws FileNotFoundException {
        System.out.println("Opening output file... " + fifoOutputFilename);
        FileOutputStream fileOutputStream = new FileOutputStream(fifoOutputFilename);
        System.out.println("Output file opened.");

        out = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
    }

    public void send(String msg) throws IOException {
        System.out.println("Sending: " + msg);

        out.write("[msg] " + msg);
        out.newLine();
        out.flush();
    }

    public void closeStream() throws IOException {
        System.out.println("Closing output file...");

        send("/quit");
        out.close();
        System.out.println("Output file closed.");

    }
}
