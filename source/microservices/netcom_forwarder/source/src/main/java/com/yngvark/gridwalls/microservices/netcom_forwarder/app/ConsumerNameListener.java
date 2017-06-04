package com.yngvark.gridwalls.microservices.netcom_forwarder.app;

import com.yngvark.gridwalls.microservices.netcom_forwarder.app.consume_input_file.FileMessageListener;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

class ConsumerNameListener implements FileMessageListener {
    private final Logger logger = getLogger(getClass());
    private String consumerName = "(empty)";
    private boolean isSet = false;

    @Override
    public void messageReceived(String msg) {
        if (isSet) {
            logger.error("Consumer name is already set! Ignoring msg: {}", msg);
            return;
        }

        consumerName = msg;
        logger.info("Setting consumer name to: {}", consumerName);
    }

    public String getConsumerName() {
        return consumerName;
    }
}
