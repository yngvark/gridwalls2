package com.yngvark.gridwalls.microservices.map_info_producer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yngvark.communicate_through_named_pipes.RetrySleeper;
import com.yngvark.communicate_through_named_pipes.input.InputFileOpener;
import com.yngvark.communicate_through_named_pipes.output.OutputFileOpener;
import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        // Args
        if (args.length < 2) {
            System.err.println("USAGE: <this program> <mkfifo input> <mkfifo output>");
            System.exit(1);
        }

        String fifoInputFilename = args[0];
        String fifoOutputFilename = args[1];

        OutputFileOpener outputFileOpener = new OutputFileOpener(fifoOutputFilename);
        InputFileOpener inputFileOpener = new InputFileOpener(fifoInputFilename);


        try {
            main(outputFileOpener, inputFileOpener);
        } catch (Throwable e) {
            logger.error("Error ocurred. Exiting.", e);
        }
    }

    static void main(OutputFileOpener outputFileOpener, InputFileOpener inputFileOpener)
            throws IOException, InterruptedException {
        RetrySleeper retrySleeper = () -> Thread.sleep(2000);
        inputFileOpener.openStream(retrySleeper); // Just so NetcomForwarder is happy. We don't need it.

        OutputFileWriter outputFileWriter = outputFileOpener.openStream(retrySleeper);
        Gson gson = new GsonBuilder().create();
        String mapInfo = gson.toJson(new MapInfo(15, 10));

        while (true) {
            outputFileWriter.write(mapInfo);
            Thread.sleep(3000);
        }
    }
}
