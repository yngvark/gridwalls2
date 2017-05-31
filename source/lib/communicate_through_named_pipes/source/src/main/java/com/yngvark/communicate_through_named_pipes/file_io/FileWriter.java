package com.yngvark.communicate_through_named_pipes.file_io;

import java.io.BufferedWriter;
import java.io.IOException;

public class FileWriter {
    private final BufferedWriter out;

    public FileWriter(BufferedWriter out) {
        this.out = out;
    }

    public void write(String msg) throws IOException {
        msg = "[msg] " + msg;
        writeRaw(msg);
    }

    private void writeRaw(String msg) throws IOException {
        System.out.println(">>> Sending: " + msg);

        out.write(msg);
        out.newLine();
        out.flush();
    }

    public void closeStream() throws IOException {
        System.out.println("Closing output file...");
        out.close();
        System.out.println("Closing output file... done.");

    }
}
