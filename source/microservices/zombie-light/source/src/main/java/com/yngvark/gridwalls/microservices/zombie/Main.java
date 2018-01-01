package com.yngvark.gridwalls.microservices.zombie;

import com.google.gson.Gson;
import com.yngvark.communicate_through_named_pipes.RetrySleeper;
import com.yngvark.communicate_through_named_pipes.input.InputFileOpener;
import com.yngvark.communicate_through_named_pipes.output.OutputFileOpener;
import com.yngvark.gridwalls.microservices.zombie.common.MapInfo;
import com.yngvark.gridwalls.microservices.zombie.common.Serializer;
import com.yngvark.gridwalls.microservices.zombie.gameloop.GameLoopRunner;
import com.yngvark.gridwalls.microservices.zombie.gameloop.Sleeper;
import com.yngvark.gridwalls.microservices.zombie.gameloop.ThreadSleeper;
import com.yngvark.gridwalls.microservices.zombie.move_zombie.Zombie;
import com.yngvark.gridwalls.microservices.zombie.move_zombie.ZombieFactory;
import com.yngvark.gridwalls.microservices.zombie.receive_map_info.MapInfoReceiver;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.Arrays;
import java.util.Random;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
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

        Sleeper sleeper = createSleeper(args);

        MapInfoReceiver mapInfoReceiver = new MapInfoReceiver(
                bufferedReader,
                bufferedWriter,
                new Serializer(new Gson()));
        MapInfo mapInfo = mapInfoReceiver.getMapInfo();
        Random random = SeedArgParser.createRandom(args);
        Zombie zombie = ZombieFactory.create(mapInfo, random);

        GameLoopRunner gameLoopRunner = GameLoopRunner.create(bufferedReader, bufferedWriter, sleeper, zombie);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down.");
            gameLoopRunner.stop();
        }));

        gameLoopRunner.run();
    }

    private static Sleeper createSleeper(String[] args) {
        Sleeper sleeper;
        if (ArrayUtils.contains(args, "--nosleep")) {
            logger.info("Using zero-wait sleeper.");
            sleeper = (timeUnit, count) -> {};
        } else {
            logger.info("Using regular-wait sleeper.");
            sleeper = new ThreadSleeper();
        }
        return sleeper;
    }
}
