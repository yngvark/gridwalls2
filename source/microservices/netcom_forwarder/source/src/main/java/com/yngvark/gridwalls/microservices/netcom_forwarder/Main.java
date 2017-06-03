package com.yngvark.gridwalls.microservices.netcom_forwarder;

import com.yngvark.communicate_through_named_pipes.input.InputFileOpener;
import com.yngvark.communicate_through_named_pipes.output.OutputFileOpener;
import com.yngvark.gridwalls.microservices.netcom_forwarder.app.App;
import com.yngvark.gridwalls.microservices.netcom_forwarder.exit_os_process.Shutdownhook;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        // Args
        if (args.length != 2) {
            System.err.println("USAGE: <this program> <mkfifo input> <mkfifo output>");
            System.exit(1);
        }
        String fifoInputFilename = args[0];
        String fifoOutputFilename = args[1];

        // Dependencies
        ExecutorService executorService = Executors.newCachedThreadPool();

        OutputFileOpener outputFileOpener = new OutputFileOpener(fifoOutputFilename);
        InputFileOpener inputFileOpener = new InputFileOpener(fifoInputFilename);

        App app = App.create(executorService, inputFileOpener, outputFileOpener);

        // Shutdownhook
        Shutdownhook shutdownhook = new Shutdownhook(app);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdownhook.run(executorService)));

        // App
        ErrorHandlingRunner errorHandlingRunner = new ErrorHandlingRunner();
        errorHandlingRunner.run(app);
    }

}
