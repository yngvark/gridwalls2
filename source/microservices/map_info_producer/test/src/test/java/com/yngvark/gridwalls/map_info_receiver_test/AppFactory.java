package com.yngvark.gridwalls.map_info_receiver_test;

import com.yngvark.communicate_through_named_pipes.input.InputFileOpener;
import com.yngvark.communicate_through_named_pipes.input.InputFileReader;
import com.yngvark.process_test_helper.InputStreamListener;
import com.yngvark.process_test_helper.ProcessStarter;
import org.slf4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.slf4j.LoggerFactory.getLogger;

public class AppFactory {
    public static final Logger logger = getLogger(AppFactory.class);

    public static App start() throws Exception {
        String from = "build/fifo_from_microservice";
        Path fromPath = Paths.get(from);
        if (Files.exists(fromPath)) {
            Files.delete(fromPath);
        }

        Runtime.getRuntime().exec("mkfifo " + from).waitFor();

        String fromAbsolutePath = Paths.get(from).toAbsolutePath().toString();

        logger.info("Current directory: {}", Paths.get("").toAbsolutePath());
        logger.info("From: {}", fromAbsolutePath);

        Process process = ProcessStarter.startProcess(
                "../source/build/install/app/bin/run",
                fromAbsolutePath);

        InputStreamListener stdoutListener = new InputStreamListener();
        stdoutListener.listenInNewThreadOn(process.getInputStream());

        InputStreamListener stderrListener = new InputStreamListener();
        stderrListener.listenInNewThreadOn(process.getErrorStream());

        InputFileOpener inputFileOpener = new InputFileOpener(from);

        logger.info("Opening input.");
        InputFileReader inputFileReader = inputFileOpener.openStream(() -> Thread.sleep(3000));
        logger.info("Streams opened.");

        App testableApp = new App();
        testableApp.process = process;
        testableApp.stdoutListener = stdoutListener;
        testableApp.stderrListener = stderrListener;
        testableApp.inputFileReader = inputFileReader;
        return testableApp;
    }
}
