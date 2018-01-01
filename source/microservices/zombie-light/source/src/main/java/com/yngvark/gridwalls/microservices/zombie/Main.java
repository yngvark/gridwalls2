package com.yngvark.gridwalls.microservices.zombie;

import com.yngvark.communicate_through_named_pipes.RetrySleeper;
import com.yngvark.communicate_through_named_pipes.input.InputFileLineReader;
import com.yngvark.communicate_through_named_pipes.input.InputFileOpener;
import com.yngvark.communicate_through_named_pipes.input.InputFileReader;
import com.yngvark.communicate_through_named_pipes.output.OutputFileOpener;
import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.gridwalls.microservices.zombie.gameloop.GameLoopRunner;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> logger.info("Shutting down.")));

        logger.info("Args length: {}. Args: {}", args.length, StringUtils.join(args, ", "));

        // Args
        if (args.length < 2) {
            System.err.println("USAGE: <this program> <mkfifo input> <mkfifo output> [--nosleep]");
            System.exit(1);
        }

        String fifoInputFilename = args[0];
        String fifoOutputFilename = args[1];

        OutputFileOpener outputFileOpener = new OutputFileOpener(fifoOutputFilename);
        InputFileOpener inputFileOpener = new InputFileOpener(fifoInputFilename);

        main(outputFileOpener, inputFileOpener, args);
    }

    static void main(OutputFileOpener outputFileOpener, InputFileOpener inputFileOpener, String[] args) {
        // Dependencies
        RetrySleeper retrySleeper = () -> Thread.sleep(1000);
        BufferedReader bufferedReader = inputFileOpener.createReader(retrySleeper);
        BufferedWriter bufferedWriter = outputFileOpener.createWriter(retrySleeper);

        GameLoopRunner gameLoopRunner = GameLoopRunner.create(bufferedReader, bufferedWriter);
        gameLoopRunner.run();
    }

}
