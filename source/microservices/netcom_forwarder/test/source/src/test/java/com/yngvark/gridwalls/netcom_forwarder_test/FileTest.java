package com.yngvark.gridwalls.netcom_forwarder_test;

import com.yngvark.communicate_through_named_pipes.input.InputFileOpener;
import com.yngvark.communicate_through_named_pipes.input.InputFileReader;
import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.gridwalls.netcom_forwarder_test.lib.InputStreamListener;
import com.yngvark.gridwalls.netcom_forwarder_test.lib.ProcessKiller;
import com.yngvark.gridwalls.netcom_forwarder_test.lib.ProcessStarter;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;

//import org.slf4j.Logger;

public class FileTest {
//    public final Logger logger = getLogger(ProcessTest.class);
    public final Logger logger = getLogger(FileTest.class);
//
//    private Logger getLogger(Class<ProcessTest2> processTestClass) {
//        return new Logger();
//    }
//    class Logger {
//
//        public void info(String s) {
//            System.out.println("info: " + s);
//        }
//
//        public void error(String s) {
//            System.out.println("error: " + s);
//        }
//    }

    @Test
    public void normal_run() throws IOException, InterruptedException, NoSuchFieldException, IllegalAccessException {
        // Given
        logger.info(Paths.get(".").toAbsolutePath().toString());

        String to = "build/to";
        String from = "build/from";
//        String to = "build/to_netcom_forwarder";
//        String from = "build/from_netcom_forwarder";
        boolean fifo = false;

        if (fifo) {
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
        }

        Process process = ProcessStarter.startProcess(
                "../../source/build/install/app/bin/run",
                to,
                from,
                "172.19.0.2");


        InputStreamListener inputStreamListener = new InputStreamListener();
        inputStreamListener.listenInNewThreadOn(process.getInputStream());

        InputStreamListener stderrListener = new InputStreamListener();
        stderrListener.listenInNewThreadOn(process.getErrorStream());

        InputFileOpener inputFileOpener = new InputFileOpener(from);
//        OutputFileOpener outputFileOpener = new OutputFileOpener(to);

        logger.info("Opening input.");
        InputFileReader inputFileReader = inputFileOpener.openStream(() -> Thread.sleep(3000));
        logger.info("Opening output.");
//        OutputFileWriter outputFileWriter = outputFileOpener.openStream(() -> Thread.sleep(3000));
        logger.info("Streams opened.");

        // When
        Thread.sleep(2000l);
        if (fifo) {
//            write(outputFileWriter, "/myNameIs netcomForwarderTest");
//            write(outputFileWriter, "/subscribeTo zombie");

            for (int i = 0; i < 3; i++) {
//                write(outputFileWriter, "/publish Hi this is networkforwarderTest: i=" + i);
                Thread.sleep(1000l);
            }

            logger.info("Closing stream. - - - -- - -");
//            outputFileWriter.closeStream();
        }

        logger.info("Reading");
        inputFileReader.consume((msg) -> {
            logger.info("<<< Msg: " + msg);
        });

//        ProcessKiller.killUnixProcess(process);

        // Then
        logger.info("Killing process.");
        ProcessKiller.waitForExitAndAssertExited(process, 5, TimeUnit.SECONDS);

        // Finally
//        logger.info("Stopping listening to inpustreamListener");
//        inputStreamListener.stopListening();

        logger.info("Closing writer");
        inputFileReader.closeStream();
        logger.info("Closing reader");
//        outputFileWriter.closeStream();
    }

    private void write(OutputFileWriter outputFileWriter, String msg) throws IOException {
        logger.info(">>> Sending: " + msg
        );
        outputFileWriter.write(msg);
    }

    class Container {
        BufferedWriter writer;
        BufferedReader reader;
    }
}

