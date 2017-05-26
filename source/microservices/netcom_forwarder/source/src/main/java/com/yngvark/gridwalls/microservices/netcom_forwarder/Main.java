package com.yngvark.gridwalls.microservices.netcom_forwarder;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length != 2)
            System.err.println("USAGE: <this program> <mkfifo input> <mkfifo output>");

        String fifoInputFilename = args[0];
        String fifoOutputFilename = args[1];

        System.out.println("Start");

        FileInputStream fileInputStream = new FileInputStream(fifoInputFilename);
        BufferedReader in = new BufferedReader(new InputStreamReader(fileInputStream));

        String read;
        while ((read = in.readLine()) != null) {
            System.out.println("Input: " + read);
        }

        in.close();
    }
}
