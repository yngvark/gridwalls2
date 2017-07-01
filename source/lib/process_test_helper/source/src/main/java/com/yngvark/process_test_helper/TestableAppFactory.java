package com.yngvark.process_test_helper;

import com.yngvark.communicate_through_named_pipes.input.InputFileOpener;
import com.yngvark.communicate_through_named_pipes.input.InputFileReader;
import com.yngvark.communicate_through_named_pipes.output.OutputFileOpener;
import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import org.slf4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.slf4j.LoggerFactory.getLogger;

public class TestableAppFactory {
    public static final Logger logger = getLogger(TestableAppFactory.class);

    public static TestableApp start() throws Exception {
        String to = "build/fifo_to_microservice";
        String from = "build/fifo_from_microservice";

        Path toPath = Paths.get(to);
        if (Files.exists(toPath)) {
            Files.delete(toPath);
        }
        Path fromPath = Paths.get(from);
        if (Files.exists(fromPath)) {
            Files.delete(fromPath);
        }

        Runtime.getRuntime().exec("mkfifo " + to).waitFor();
        Runtime.getRuntime().exec("mkfifo " + from).waitFor();

        String toAbsolutePath = Paths.get(to).toAbsolutePath().toString();
        String fromAbsolutePath = Paths.get(from).toAbsolutePath().toString();

        logger.info("Current directory: {}", Paths.get("").toAbsolutePath());
        logger.info("To: {}", toAbsolutePath);
        logger.info("From: {}", fromAbsolutePath);

        Process process = ProcessStarter.startProcess(
                "../source/build/install/app/bin/run",
                toAbsolutePath,
                fromAbsolutePath);

        InputStreamListener stdoutListener = new InputStreamListener();
        stdoutListener.listenInNewThreadOn(process.getInputStream());

        InputStreamListener stderrListener = new InputStreamListener();
        stderrListener.listenInNewThreadOn(process.getErrorStream());

        InputFileOpener inputFileOpener = new InputFileOpener(from);
        OutputFileOpener outputFileOpener = new OutputFileOpener(to);

        logger.info("Opening output.");
        OutputFileWriter outputFileWriter = outputFileOpener.openStream(() -> Thread.sleep(3000));
        logger.info("Opening input.");
        InputFileReader inputFileReader = inputFileOpener.openStream(() -> Thread.sleep(3000));
        logger.info("Streams opened.");

        TestableApp testableApp = new TestableApp();
        testableApp.process = process;
        testableApp.stdoutListener = stdoutListener;
        testableApp.stderrListener = stderrListener;
        testableApp.inputFileReader = inputFileReader;
        testableApp.outputFileWriter = outputFileWriter;
        return testableApp;
    }

}
