package com.yngvark.gridwalls.microservices.netcom_forwarder;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class NetcomConsumer {
    public void consume(String fifoInputFilename) throws IOException {
        System.out.println("Consuming file: " + fifoInputFilename);

        FileInputStream fileInputStream = new FileInputStream(fifoInputFilename);
        BufferedReader in = new BufferedReader(new InputStreamReader(fileInputStream));

        String read;
        while ((read = in.readLine()) != null) {
            System.out.println("Received from microservice: " + read);
        }

        in.close();
    }
}
