package com.yngvark.gridwalls.microservices.netcom_forwarder.app;

import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import org.slf4j.Logger;

import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

class NetworkToFileHub {
    private final Logger logger = getLogger(getClass());
    private final OutputFileWriter outputFileWriter;

    private boolean run = true;

    public NetworkToFileHub(OutputFileWriter outputFileWriter) {
        this.outputFileWriter = outputFileWriter;
    }

    public void consumeAndForward() throws IOException, InterruptedException {
        for (int i = 0; i < 10 && run; i++) {
            String msg = "NETWORK UHU This is from Weather, line " + i; // Should come from rabbitMq
            outputFileWriter.write(msg);
            Thread.sleep(1000);
        }
    }

    public void stop() {
        logger.info("Stopping " + getClass().getSimpleName());
        run = false;
    }
}
