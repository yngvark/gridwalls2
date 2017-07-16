package com.yngvark.named_piped_app_runner;

import com.yngvark.communicate_through_named_pipes.input.InputFileLineReader;
import com.yngvark.communicate_through_named_pipes.input.InputFileReader;
import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;

import java.util.concurrent.TimeUnit;

public class NamedPipeProcess {
    public Process process;
    public InputStreamListener stdoutListener;
    public InputStreamListener stderrListener;
    public InputFileLineReader inputFileLineReader;
    public OutputFileWriter outputFileWriter;

    public void stop() throws Exception {
        stdoutListener.stopListening();
        stderrListener.stopListening();

        inputFileLineReader.closeStream();
        outputFileWriter.closeStream();

        ProcessKiller.killUnixProcess(process);
        ProcessKiller.waitForExitAndAssertExited(process, 5, TimeUnit.SECONDS);
    }
}
