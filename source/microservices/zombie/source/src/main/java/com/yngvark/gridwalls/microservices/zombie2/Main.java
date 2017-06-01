package com.yngvark.gridwalls.microservices.zombie2;

import com.yngvark.communicate_through_named_pipes.file_io.FileConsumer;
import com.yngvark.communicate_through_named_pipes.file_io.FileOpener;
import com.yngvark.gridwalls.microservices.zombie2.app.App;
import com.yngvark.gridwalls.microservices.zombie2.exit_os_process.Shutdownhook;
import com.yngvark.os_process_exiter.ExecutorServiceExiter;

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
        FileOpener fileOpener = new FileOpener(fifoOutputFilename);
        FileConsumer fileConsumer = new FileConsumer(fifoInputFilename);

        App app = App.create(executorService, fileOpener, fileConsumer);

        // Shutdownhook
        Shutdownhook shutdownhook = new Shutdownhook(app);
        Runtime.getRuntime().addShutdownHook(new Thread(shutdownhook::run));

        // App
        ErrorHandlingRunner errorHandlingRunner = new ErrorHandlingRunner();
        errorHandlingRunner.run(app);

        // Exit
        ExecutorServiceExiter.exitGracefully(executorService);
    }

}
