package com.yngvark.gridwalls.microservices.netcom_forwarder;

import com.yngvark.communicate_through_named_pipes.input.InputFileOpener;
import com.yngvark.communicate_through_named_pipes.output.OutputFileOpener;
import com.yngvark.gridwalls.microservices.netcom_forwarder.app.App;
import com.yngvark.gridwalls.microservices.netcom_forwarder.exit_os_process.Shutdownhook;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.slf4j.LoggerFactory.getLogger;

public class Main {
    private static final Logger logger = getLogger(Main.class);

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        logger.info("Args length: " + args.length);
        logger.info("Args: " + StringUtils.join(args));

        // Args
        if (args.length < 2) {
            logger.error("USAGE: <this program> <mkfifo input> <mkfifo output> [host ip/name]");
            System.exit(1);
        }

        String fifoInputFilename = args[0];
        String fifoOutputFilename = args[1];
        String host = args.length == 3 ? args[2] : "rabbitmq";

        // Dependencies
        ExecutorService executorService = Executors.newCachedThreadPool();

        OutputFileOpener outputFileOpener = new OutputFileOpener(fifoOutputFilename);
        InputFileOpener inputFileOpener = new InputFileOpener(fifoInputFilename);

        App app = App.create(executorService, inputFileOpener, outputFileOpener, host);

        // Shutdownhook
        Shutdownhook shutdownhook = new Shutdownhook(app);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdownhook.run(executorService)));

        // App
        ErrorHandlingRunner errorHandlingRunner = new ErrorHandlingRunner();
        errorHandlingRunner.run(app);
    }

}
