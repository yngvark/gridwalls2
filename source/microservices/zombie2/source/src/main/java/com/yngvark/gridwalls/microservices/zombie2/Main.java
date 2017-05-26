package com.yngvark.gridwalls.microservices.zombie2;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length != 2)
            System.err.println("USAGE: <this program> <mkfifo input> <mkfifo output>");

        String fifoInputFilename = args[0];
        String fifoOutputFilename = args[1];

        System.out.println("Start");<

        FileOutputStream fileOutputStream = new FileOutputStream(fifoOutputFilename);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

        for (int i = 0; i < 3; i++) {
            out.write("Hey this is from zombie, line " + i);
            out.newLine();
            out.flush();
            Thread.sleep(1000);
        }

        out.close();
    }
}
