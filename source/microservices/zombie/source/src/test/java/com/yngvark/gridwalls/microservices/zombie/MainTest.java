package com.yngvark.gridwalls.microservices.zombie;

import com.yngvark.communicate_through_named_pipes.input.InputFileOpener;
import com.yngvark.communicate_through_named_pipes.input.InputFileReader;
import com.yngvark.communicate_through_named_pipes.output.OutputFileOpener;
import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.process_test_helper.InputStreamListener;
import com.yngvark.process_test_helper.ProcessKiller;
import com.yngvark.process_test_helper.ProcessStarter;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newCachedThreadPool;
import static org.junit.jupiter.api.Assertions.*;
import static org.slf4j.LoggerFactory.getLogger;

class MainTest {
    private final Logger logger = getLogger(getClass());

    public App startApp(String to, String from) throws Exception {
        logger.info(Paths.get(".").toAbsolutePath().toString());

        Path toPath = Paths.get(from);
        if (Files.exists(toPath)) {
            Files.delete(toPath);
        }
        Path fromPath = Paths.get(from);
        if (Files.exists(fromPath)) {
            Files.delete(fromPath);
        }
        Runtime.getRuntime().exec("mkfifo " + to).waitFor();
        Runtime.getRuntime().exec("mkfifo " + from).waitFor();

        OutputFileOpener outputFileOpener = new OutputFileOpener(to);
        InputFileOpener inputFileOpener = new InputFileOpener(from);

        logger.info("Opening output.");
        OutputFileWriter outputFileWriter = outputFileOpener.openStream(() -> Thread.sleep(3000));
        logger.info("Opening input: ");
        InputFileReader inputFileReader = inputFileOpener.openStream(() -> Thread.sleep(3000));

        logger.info("Streams opened.");

        App app = new App();
        app.inputFileReader = inputFileReader;
        app.outputFileWriter = outputFileWriter;
        return app;
    }

    class App {
        InputFileReader inputFileReader;
        OutputFileWriter outputFileWriter;

        public void stopAndFreeResources() throws Exception {
            inputFileReader.closeStream();
            outputFileWriter.closeStream();
        }
    }
    @Test
    void main() throws Exception {
        String to = "build/to_app";
        String from = "build/from_app";

        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(() -> {
            try {
                Main.main(new String[] { to, from });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        App app = startApp(to, from);

        Thread.sleep(2000l);
        app.stopAndFreeResources();
    }

}