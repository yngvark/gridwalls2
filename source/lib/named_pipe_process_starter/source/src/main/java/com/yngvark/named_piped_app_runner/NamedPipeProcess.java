package com.yngvark.named_piped_app_runner;

import com.yngvark.communicate_through_named_pipes.input.InputFileLineReader;
import com.yngvark.communicate_through_named_pipes.input.InputFileReader;
import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class NamedPipeProcess {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public Process process;
    public InputStreamListener stdoutListener;
    public InputStreamListener stderrListener;
    public InputFileLineReader inputFileLineReader;
    public OutputFileWriter outputFileWriter;

    public void stop() throws Exception {
        logger.info("Stopping {}", getClass().getSimpleName());

        stdoutListener.stopListening();
        stderrListener.stopListening();

        /*
         * With Java stream readers it's impossible to shut down a stream from the receiving end of the stream.
         * We have to shut down the stream from the producing side, i.e. kill the process.
         */

        ProcessKiller.killUnixProcess(process);
        ProcessKiller.waitForExitAndAssertExited(process, 5, TimeUnit.SECONDS);

        inputFileLineReader.closeStream();
        outputFileWriter.closeStream();
    }
}
