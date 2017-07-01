package com.yngvark.process_test_helper;

import com.yngvark.communicate_through_named_pipes.input.InputFileReader;
import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;

import java.util.concurrent.TimeUnit;

public class App {
    public Process process;
    public InputStreamListener stdoutListener;
    public InputStreamListener stderrListener;
    public InputFileReader inputFileReader;
    public OutputFileWriter outputFileWriter;

    public void stop() throws Exception {
        stdoutListener.stopListening();
        stderrListener.stopListening();

        inputFileReader.closeStream();
        outputFileWriter.closeStream();

        ProcessKiller.killUnixProcess(process);
        ProcessKiller.waitForExitAndAssertExited(process, 5, TimeUnit.SECONDS);
    }
}
