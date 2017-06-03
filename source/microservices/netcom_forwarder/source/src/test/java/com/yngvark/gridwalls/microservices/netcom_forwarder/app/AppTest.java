package com.yngvark.gridwalls.microservices.netcom_forwarder.app;

import com.yngvark.communicate_through_named_pipes.RetrySleeper;
import com.yngvark.communicate_through_named_pipes.input.InputFileOpener;
import com.yngvark.communicate_through_named_pipes.input.InputFileReader;
import com.yngvark.communicate_through_named_pipes.output.OutputFileOpener;
import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.gridwalls.microservices.netcom_forwarder.ErrorHandlingTestRunner;
import com.yngvark.gridwalls.microservices.netcom_forwarder.app.forward_msgs.NetworkMsgListenerFactoryFactory;
import com.yngvark.gridwalls.microservices.netcom_forwarder.app.forward_msgs.NetworkToMsForwarder;
import com.yngvark.gridwalls.microservices.netcom_forwarder.exit_os_process.Shutdownhook;
import com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq.BlockingRabbitConsumer;
import com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq.RabbitBrokerConnecter;
import com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq.RabbitMessageListener;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.slf4j.LoggerFactory.getLogger;

class AppTest {
    private final Logger logger = getLogger(getClass());

    @Test
    void nothing_should_crash() throws Throwable {
        /* This method should resemble the Main class as much as possible, so that we test as close to reality as
        possible.*/

        /* Dependencies */
        ExecutorService executorService = Executors.newCachedThreadPool();
        RabbitBrokerConnecter rabbitBrokerConnecter = mock(RabbitBrokerConnecter.class);

        RetrySleeper retrySleeper = () -> {
            // Do not sleep at all.
        };

        InputFileOpener microserviceReaderOpener = mock(InputFileOpener.class);
        when(microserviceReaderOpener.openStream(any(RetrySleeper.class))).thenReturn(mock(InputFileReader.class));

        OutputFileOpener microserviceWriterOpener = mock(OutputFileOpener.class);
        when(microserviceWriterOpener.openStream(any(RetrySleeper.class))).thenReturn(mock(OutputFileWriter.class));

        BlockingRabbitConsumer blockingRabbitConsumer = mock(BlockingRabbitConsumer.class);

        doAnswer((invocationOnMock) -> {
            logger.info("Test messages are now being consumed.");
            RabbitMessageListener rabbitMessageListener = invocationOnMock.getArgument(2);
            rabbitMessageListener.messageReceived("hello");
            rabbitMessageListener.messageReceived("IIIIIITS ME");
            rabbitMessageListener.messageReceived("WERE YOU EVER WONDERING ABOUT STUFF");
            return null;
        }).when(blockingRabbitConsumer).consume(isNull(), any(String.class), any(RabbitMessageListener.class));

        // Init app
        App app = new App(
                executorService,
                rabbitBrokerConnecter,
                retrySleeper,
                microserviceReaderOpener,
                microserviceWriterOpener,
                new NetworkToMsForwarder(
                        NetworkMsgListenerFactoryFactory.create(),
                        blockingRabbitConsumer
                )
        );

        // Shutdownhook
        Shutdownhook shutdownhook = new Shutdownhook(app);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdownhook.run(executorService)));

        ErrorHandlingTestRunner errorHandlingTestRunner = ErrorHandlingTestRunner.create();

        // When
        errorHandlingTestRunner.run(app);

        // Then
        logger.info("HIH");
        verify(blockingRabbitConsumer).consume(isNull(), any(String.class), any(RabbitMessageListener.class));
    }
}