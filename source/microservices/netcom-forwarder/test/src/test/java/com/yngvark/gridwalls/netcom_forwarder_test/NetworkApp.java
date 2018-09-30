package com.yngvark.gridwalls.netcom_forwarder_test;

import com.yngvark.communicate_through_named_pipes.input.InputFileReader;
import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.gridwalls.rabbitmq.RabbitConnection;
import com.yngvark.named_piped_app_runner.InputStreamListener;
import com.yngvark.named_piped_app_runner.ProcessKiller;

import java.util.concurrent.TimeUnit;

class NetworkApp {
    String host;
    RabbitConnection rabbitConnection;
    Process process;
    InputStreamListener stdoutListener;
    InputStreamListener stderrListener;
    InputFileReader inputFileReader;
    OutputFileWriter outputFileWriter;

    public void stopAndFreeResources() throws Exception {
        stdoutListener.stopListening();
        stderrListener.stopListening();

        inputFileReader.closeStream();
        outputFileWriter.closeStream();

        rabbitConnection.disconnectIfConnected();

        ProcessKiller.killUnixProcess(process);
        ProcessKiller.waitForExitAndAssertExited(process, 5, TimeUnit.SECONDS);
    }
}
