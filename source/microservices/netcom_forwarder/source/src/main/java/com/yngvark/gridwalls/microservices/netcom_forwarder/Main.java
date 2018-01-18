package com.yngvark.gridwalls.microservices.netcom_forwarder;

import com.yngvark.communicate_through_named_pipes.RetrySleeper;
import com.yngvark.communicate_through_named_pipes.input.InputFileOpener;
import com.yngvark.communicate_through_named_pipes.input.InputFileReader;
import com.yngvark.communicate_through_named_pipes.output.OutputFileOpener;
import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.gridwalls.microservices.netcom_forwarder.app.App;
import com.yngvark.gridwalls.microservices.netcom_forwarder.exit_os_process.Shutdownhook;
import com.yngvark.gridwalls.rabbitmq.RabbitBrokerConnecter;
import com.yngvark.gridwalls.rabbitmq.RabbitConnection;
import com.yngvark.os_process_exiter.ExecutorServiceExiter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.slf4j.LoggerFactory.getLogger;

public class Main {
    private static final Logger logger = getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Netcom Forwarder version 0.1");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> logger.info("Received exit signal")));
        logger.info("Args: " + StringUtils.join(args, ' '));

        // Args
        if (args.length < 2) {
            logger.error("USAGE: <this program> <mkfifo input> <mkfifo output> [host ip/name]");
            System.exit(1);
        }

        String fifoInputFilename = args[0];
        String fifoOutputFilename = args[1];
        String host = args.length == 3 ? args[2] : "rabbitmq";

        createNamedPipeIfNotExists(fifoInputFilename);
        createNamedPipeIfNotExists(fifoOutputFilename);

        // Dependencies
        OutputFileOpener outputFileOpener = new OutputFileOpener(fifoOutputFilename);
        InputFileOpener inputFileOpener = new InputFileOpener(fifoInputFilename);

        main(outputFileOpener, inputFileOpener, host);
    }

    private static void createNamedPipeIfNotExists(String filename) {
        Path path = Paths.get(filename);
        String pathAbsolute = path.toAbsolutePath().toString();
        if (Files.exists(path)) {
            logger.info("Named pipe already exist, not doing anything: " + filename);
        } else {
            try {
                String parentDir = path.getParent().toAbsolutePath().toString();
                logger.info("Creating parent dir if not exists: mkdir -p {}", parentDir);
                Runtime.getRuntime().exec("mkdir -p " + parentDir).waitFor();
                logger.info("Creating named pipe: {}", pathAbsolute);
                Runtime.getRuntime().exec("mkfifo " + pathAbsolute).waitFor();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void main(OutputFileOpener outputFileOpener, InputFileOpener inputFileOpener, String host) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        CompletionService completionService = new ExecutorCompletionService(executorService);

        RabbitBrokerConnecter rabbitBrokerConnecter = new RabbitBrokerConnecter(host);
        logger.info("Connecting to network...");
        RabbitConnection rabbitConnection = rabbitBrokerConnecter.connect();
        logger.info("Connecting to network... done.");

        RetrySleeper retrySleeper = () -> {
            logger.info("Retrying...");
            Thread.sleep(2000);
            logger.info("Sleep done");
        };
        OutputFileWriter outputFileWriter = outputFileOpener.openStream(retrySleeper);
        InputFileReader inputFileReader = inputFileOpener.openStream(retrySleeper);

        App app = App.create(completionService, rabbitConnection, inputFileReader, outputFileWriter);

        // Shutdownhook
        Shutdownhook shutdownhook = new Shutdownhook(app);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdownhook.run(executorService)));

        // App
        ErrorHandlingRunner errorHandlingRunner = new ErrorHandlingRunner();
        errorHandlingRunner.run(app);

        // Exit
        ExecutorServiceExiter.exitGracefully(executorService);
    }

}
