package com.gridwalls.yngvark.integration_tests.zombie_should_use_map_info;

import com.yngvark.communicate_through_named_pipes.input.InputFileOpener;
import com.yngvark.communicate_through_named_pipes.input.InputFileReader;
import com.yngvark.communicate_through_named_pipes.output.OutputFileOpener;
import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

class TestAppFactory {
    private static final Logger logger = LoggerFactory.getLogger(TestAppFactory.class);

    public static TestApp create() {
        String fifoInputFilename = System.getProperty("test.fifo_input_filename");
        String fifoOutputFilename = System.getProperty("test.fifo_output_filename");
        InputFileOpener inputFileOpener = new InputFileOpener(fifoInputFilename);
        OutputFileOpener outputFileOpener = new OutputFileOpener(fifoOutputFilename);

        TestApp testApp = new TestApp();
        Future f = Executors.newCachedThreadPool().submit(() -> {
            logger.info("Opening input.");

            testApp.inputFileReader = inputFileOpener.openStream(() -> {
                Thread.sleep(3000L);
            });
            logger.info("Opening output.");
            testApp.outputFileWriter = outputFileOpener.openStream(() -> {
                Thread.sleep(3000L);
            });
        });

        try {
            f.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error("No one opened stream on other side", e);
            throw new RuntimeException(e);
        }

        return testApp;
    }
}
