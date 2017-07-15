package com.yngvark.gridwalls.microservices.map_info_producer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yngvark.communicate_through_named_pipes.RetrySleeper;
import com.yngvark.communicate_through_named_pipes.input.InputFileOpener;
import com.yngvark.communicate_through_named_pipes.input.InputFileReader;
import com.yngvark.communicate_through_named_pipes.output.OutputFileOpener;
import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import org.apache.commons.lang3.StringUtils;
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
        InputFileReader inputFileReader = inputFileOpener.openStream(retrySleeper);

        OutputFileWriter outputFileWriter = outputFileOpener.openStream(retrySleeper);
        outputFileWriter.write("/subscribeTo MapInfoRequests");

        Gson gson = new Gson();

        inputFileReader.consume((msg) -> {
            MapInfoRequest request = parseRequest(gson, msg);
            reply(outputFileWriter, gson, request);
        });

        logger.info("Done consuming.");
        outputFileWriter.closeStream();
    }

    private static MapInfoRequest parseRequest(Gson gson, String msg) {
        String[] parts = StringUtils.split(msg);
        return gson.fromJson(parts[1], MapInfoRequest.class);
    }

    private static void reply(OutputFileWriter outputFileWriter, Gson gson, MapInfoRequest request) {
        String mapInfo = gson.toJson(new MapInfo(15, 10));
        outputFileWriter.write("/publishTo " + request.replyToTopic + " " + mapInfo);
    }
}
