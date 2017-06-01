package com.yngvark.communicate_through_named_pipes.consume;

import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;

public class FileConsumer {
    private final Logger logger = getLogger(getClass());
    private final String fifoInputFilename;
    private final FileOpenRetrySleeper fileOpenRetrySleeper;

    private boolean run = true;
    private BufferedReader in;

    public FileConsumer(String fifoInputFilename) {
        this.fifoInputFilename = fifoInputFilename;
        fileOpenRetrySleeper = () -> Thread.sleep(TimeUnit.SECONDS.toMillis(3));
    }

    public FileConsumer(String fifoInputFilename, FileOpenRetrySleeper fileOpenRetrySleeper) {
        this.fifoInputFilename = fifoInputFilename;
        this.fileOpenRetrySleeper = fileOpenRetrySleeper;
    }

    public void consume() throws IOException {
        logger.info("Opening consume file... " + fifoInputFilename);

        while (!Files.exists(Paths.get(fifoInputFilename))) {
            try {
                logger.warn("Could not find file. Attempting again soon.");
                fileOpenRetrySleeper.sleep();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        FileInputStream fileInputStream = new FileInputStream(fifoInputFilename);
        logger.info("Opening consume file... done.");

        in = new BufferedReader(new InputStreamReader(fileInputStream));

        logger.info("Consume: start.");

        String read;
        while ((read = in.readLine()) != null && run) {
            logger.info("<<< From other side: " + read);
        }

        if (read == null) {
            logger.info("Consume file stream was closed from other side.");
        }

        in.close();

        logger.info("");
        logger.info("Consume: done.");
    }

    public void stopConsuming() {
        logger.info("Stopping consuming input file...");
        run = false;

        if (in != null) {
            try {
                in.close();
                in = null;
            } catch (IOException e) {
                logger.info("Caught exception when stopping consuming.");
                e.printStackTrace();
            }
        }

        logger.info("Stopping consuming input file... done");
    }
}
