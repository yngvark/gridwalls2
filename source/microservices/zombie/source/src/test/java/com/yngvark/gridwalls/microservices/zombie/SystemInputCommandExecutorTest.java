package com.yngvark.gridwalls.microservices.zombie;

import com.yngvark.gridwalls.test_utils.SyncronousExecutorService;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import static org.junit.Assert.*;

public class SystemInputCommandExecutorTest {
    @Test
    public void should_not_run_command_with_wrong_identifier() throws Exception {
        // Given
        SystemInputCommandExecutor executor = new SystemInputCommandExecutor(new SyncronousExecutorService());

        CommandReceiver commandReceiver = new CommandReceiver();
        executor.handleCommand("connect", () -> {
            commandReceiver.setReceivedCommand("connect");
        });

        OutputStreamWriter writer = createOutputStreamWriterForSystemIn();
        writer.write("someOtherCommand\n");
        writer.close();

        // When
        executor.readFromStdIn();

        // Then
        assertEquals("", commandReceiver.getReceivedCommand());
    }

    private OutputStreamWriter createOutputStreamWriterForSystemIn() throws IOException {
        PipedInputStream stdin = new PipedInputStream();
        OutputStream stdout = new PipedOutputStream(stdin);
        OutputStreamWriter writer = new OutputStreamWriter(stdout);
        System.setIn(stdin);
        return writer;
    }

    @Test
    public void should_run_command_with_correct_identifier() throws Exception {
        // Given
        SystemInputCommandExecutor executor = new SystemInputCommandExecutor(new SyncronousExecutorService());
        CommandReceiver commandReceiver = new CommandReceiver();

        // When
        executor.handleCommand("sayHello", () -> {
            commandReceiver.setReceivedCommand("hi there");
        });

        OutputStreamWriter writer = createOutputStreamWriterForSystemIn();
        writer.write("sayHello\n");
        writer.close();

        // When
        executor.readFromStdIn();

        // Then
        assertEquals("hi there", commandReceiver.getReceivedCommand());
    }

    private class CommandReceiver {
        private String receivedCommand = "";

        public void setReceivedCommand(String receivedCommand) {
            this.receivedCommand = receivedCommand;
        }

        public String getReceivedCommand() {
            return receivedCommand;
        }
    }
}