package com.yngvark.gridwalls.map_info_receiver_test;

import com.yngvark.communicate_through_named_pipes.input.InputFileReader;
import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.process_test_helper.InputStreamListener;
import com.yngvark.process_test_helper.ProcessKiller;
import com.yngvark.process_test_helper.TestableAppFactory;
import org.slf4j.Logger;

import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;

public class App {
    public final Logger logger = getLogger(getClass());

    public Process process;
    public InputStreamListener stdoutListener;
    public InputStreamListener stderrListener;
    public InputFileReader inputFileReader;

    public void stop() throws Exception {
        stdoutListener.stopListening();
        stderrListener.stopListening();

        inputFileReader.closeStream();

        ProcessKiller.killUnixProcess(process);
        ProcessKiller.waitForExitAndAssertExited(process, 5, TimeUnit.SECONDS);
    }

}
