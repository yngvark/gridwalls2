package com.yngvark.gridwalls.microservices.zombie2.file_io;

import java.io.BufferedWriter;
import java.io.IOException;

public class FileWriter {
    private final BufferedWriter out;

    public FileWriter(BufferedWriter out) {
        this.out = out;
    }

    public void write(String msg) throws IOException {
        msg = "[msg] " + msg;
        System.out.println(">>> Sending: " + msg);

        out.write(msg);
        out.newLine();
        out.flush();
    }

    public void closeStream() throws IOException {
        System.out.println("Closing output file...");

        write("/quit");
        out.close();
        System.out.println("Output file closed.");

    }
}
