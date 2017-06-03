package com.yngvark.gridwalls.microservices.netcom_forwarder.app.forward_msgs;

import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq.BlockingRabbitConsumer;
import com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq.RabbitConnection;
import com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq.RabbitMessageListener;
import org.slf4j.Logger;

import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

public class NetworkToMsForwarder {
    private final Logger logger = getLogger(getClass());

    private final NetworkMsgListenerFactory networkMsgListenerFactory;
    private final BlockingRabbitConsumer blockingRabbitConsumer;

    public static NetworkToMsForwarder create() {
        return new NetworkToMsForwarder(
                new NetworkMsgListenerFactory(),
                BlockingRabbitConsumer.create());
    }

    public NetworkToMsForwarder(
            NetworkMsgListenerFactory networkMsgListenerFactory,
            BlockingRabbitConsumer blockingRabbitConsumer) {
        this.networkMsgListenerFactory = networkMsgListenerFactory;
        this.blockingRabbitConsumer = blockingRabbitConsumer;
    }

    public void consumeAndForward(RabbitConnection rabbitConnection, OutputFileWriter microserviceWriter)
            throws IOException, InterruptedException {
        RabbitMessageListener rabbitMessageListener = networkMsgListenerFactory.create(microserviceWriter);
        blockingRabbitConsumer.consume(rabbitConnection, "game", rabbitMessageListener);
        logger.info("Consuming done.");
    }

    public void stop() {
        if (blockingRabbitConsumer != null)
            blockingRabbitConsumer.stop();
    }

}
