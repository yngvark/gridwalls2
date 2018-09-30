package com.yngvark.gridwalls.microservices.netcom_forwarder.app.consume_input_file;

import com.yngvark.communicate_through_named_pipes.input.InputFileReader;
import com.yngvark.communicate_through_named_pipes.input.MessageListener;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

class InputFileConsumerTest {
    @Test
    void consume() throws IOException {
        InputFileReader inputFileReader = mock(InputFileReader.class);

        InputFileConsumer inputFileConsumer = new InputFileConsumer(inputFileReader);
        doAnswer((invocation) -> {
            System.out.println("doAnswer");
            MessageListener messageListener = invocation.getArgument(0);
            System.out.println(messageListener);

            messageListener.messageReceived("/join something");
            return null;
        }).when(inputFileReader).consume(any());

        List<String> commandCalled = new ArrayList<>();
        inputFileConsumer.addMessageListener((msg) -> {
            System.out.println("msg received");
            commandCalled.add(msg);
        }, "/join");

        inputFileConsumer.addMessageListener(new Arne(), "/join2");

        // When
        inputFileConsumer.consume();

        // Then
        assertEquals("something", commandCalled.get(0));
    }

    class Arne implements FileMessageListener {

        @Override
        public void messageReceived(String s) {
            System.out.println("HEHHER222222222");

        }
    }

}