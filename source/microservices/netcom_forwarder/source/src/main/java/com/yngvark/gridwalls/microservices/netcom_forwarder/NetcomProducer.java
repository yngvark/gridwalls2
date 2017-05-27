package com.yngvark.gridwalls.microservices.netcom_forwarder;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class NetcomProducer {
    public void produce(String fifoOutputFilename) throws IOException, InterruptedException {
        System.out.println("Opening file: " + fifoOutputFilename);

        FileOutputStream fileOutputStream = new FileOutputStream(fifoOutputFilename);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

        System.out.println("Starting now...");

        for (int i = 0; i < 8; i++) {
            String msg = "Hey this is from WeatherService, line " + i;
            out.write(msg);
            System.out.println("Forwarding to microservice: " + msg);
            out.newLine();
            out.flush();
            Thread.sleep(1000);
        }

        out.close();
    }
}
