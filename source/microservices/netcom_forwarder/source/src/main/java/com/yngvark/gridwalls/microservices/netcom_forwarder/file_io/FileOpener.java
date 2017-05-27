package com.yngvark.gridwalls.microservices.netcom_forwarder.file_io;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class FileOpener {
    private final String fifoOutputFilename;

    public FileOpener(String fifoOutputFilename) {
        this.fifoOutputFilename = fifoOutputFilename;
    }

    public FileWriter openStream() throws FileNotFoundException {
        System.out.println("Opening output file... " + fifoOutputFilename);
        FileOutputStream fileOutputStream = new FileOutputStream(fifoOutputFilename);
        System.out.println("Opening output file... done.");

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

        return new FileWriter(out);
    }

}
