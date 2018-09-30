package com.yngvark.gridwalls.microservices.netcom_forwarder.app.consume_input_file;

import com.yngvark.communicate_through_named_pipes.input.InputFileReader;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

public class InputFileConsumer {
    private final Logger logger = getLogger(getClass());
    private final Map<Command, FileMessageListener> listeners = new HashMap<>();
    private final InputFileReader inputFileReader;

    public InputFileConsumer(InputFileReader inputFileReader) {
        this.inputFileReader = inputFileReader;
    }

    public void addMessageListener(FileMessageListener fileMessageListener, String commandToListenFor) {
        logger.info("Adding message listener for command: " + commandToListenFor);
        listeners.put(new Command(commandToListenFor), fileMessageListener);
    }

    public void consume() throws IOException {
        inputFileReader.consume((msgRaw) -> {
            Message msg = parse(msgRaw);
            logger.info("<<< From input: " + msg);

            if (listeners.containsKey(msg.getCommand())) {
                listeners.get(msg.getCommand()).messageReceived(msg.getContents());
            } else {
                logger.warn("No listener for msg.");
            }
        });
    }

    private Message parse(String msg) {
        String[] parts = StringUtils.split(msg, " ", 2);
        if (parts.length != 2) {
            logger.error("Cannot parse raw message: {}", msg);
            return new Message(new Command("(unknown)"), msg);
        }

        return new Message(new Command(parts[0]), parts[1]);
    }
}
