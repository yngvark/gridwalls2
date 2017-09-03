package com.yngvark.gridwalls.microservices.zombie;

import com.yngvark.communicate_through_named_pipes.RetrySleeper;
import com.yngvark.communicate_through_named_pipes.input.InputFileOpener;
import com.yngvark.communicate_through_named_pipes.input.InputFileReader;
import com.yngvark.communicate_through_named_pipes.output.OutputFileOpener;
import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.gridwalls.microservices.zombie.exit_os_process.Shutdownhook;
import com.yngvark.gridwalls.microservices.zombie.run_app.App;
import com.yngvark.gridwalls.microservices.zombie.run_game.Sleeper;
import com.yngvark.os_process_exiter.ExecutorServiceExiter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
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
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);

        RetrySleeper retrySleeper = () -> Thread.sleep(1000);
        InputFileReader inputFileReader = inputFileOpener.openStream(retrySleeper);
        OutputFileWriter outputFileWriter = outputFileOpener.openStream(retrySleeper);

        Sleeper sleeper = createSleeper(args);
        Random random = createRandom(args);
        App app = App.create(sleeper, random, inputFileReader, outputFileWriter, executorService);

        // Shutdownhook
        Shutdownhook shutdownhook = new Shutdownhook(app);
        Runtime.getRuntime().addShutdownHook(new Thread(shutdownhook::run));

        // App
        ErrorHandlingRunner errorHandlingRunner = new ErrorHandlingRunner();
        errorHandlingRunner.run(app);

        // Exit
        ExecutorServiceExiter.exitGracefully(executorService);
    }

    private static Sleeper createSleeper(String[] args) {
        Sleeper sleeper;
        if (ArrayUtils.contains(args, "--nosleep")) {
            logger.info("Using zero-wait sleeper.");
            sleeper = (long millis) -> {};
        } else {
            sleeper = new ThreadSleeper();
        }
        return sleeper;
    }

    private static Random createRandom(String[] args) {
        if (ArrayUtils.contains(args, "-seed=")) {
            int pos = ArrayUtils.indexOf(args, "-seed=");
            String seed = args[pos].substring(6);
            return new Random(Integer.parseInt(seed));
        } else {
            return new Random();
        }
    }

}
