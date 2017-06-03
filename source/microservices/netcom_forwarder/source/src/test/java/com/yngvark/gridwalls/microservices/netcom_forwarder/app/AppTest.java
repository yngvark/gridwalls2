package com.yngvark.gridwalls.microservices.netcom_forwarder.app;

import com.yngvark.communicate_through_named_pipes.RetrySleeper;
import com.yngvark.communicate_through_named_pipes.input.InputFileOpener;
import com.yngvark.communicate_through_named_pipes.input.InputFileReader;
import com.yngvark.communicate_through_named_pipes.input.MessageListener;
import com.yngvark.communicate_through_named_pipes.output.OutputFileOpener;
import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.gridwalls.microservices.netcom_forwarder.ErrorHandlingTestRunner;
import com.yngvark.gridwalls.microservices.netcom_forwarder.app.forward_msgs_to_microservice.NetworkMsgListenerFactoryFactory;
import com.yngvark.gridwalls.microservices.netcom_forwarder.app.forward_msgs_to_microservice.NetworkToMsForwarder;
import com.yngvark.gridwalls.microservices.netcom_forwarder.app.forward_msgs_to_network.MsToNetworkForwarder;
import com.yngvark.gridwalls.microservices.netcom_forwarder.app.forward_msgs_to_network.RabbitPublisherFactory;
import com.yngvark.gridwalls.microservices.netcom_forwarder.exit_os_process.Shutdownhook;
import com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq.BlockingRabbitConsumer;
import com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq.RabbitBrokerConnecter;
import com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq.RabbitConnection;
import com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq.RabbitMessageListener;
import com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq.RabbitPublisher;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.slf4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Notes about the tests in this class:
 *
 * - Do not att shutdownhook using Runtime.getRuntime().addShutdownHook, because then it will exist when
 * test is done. We want to do verifications after shutdownhook has completed, and we would like test coverage
 * report to include the code that gets run via the shutdownhook.
 */
class AppTest {
    private final Logger logger = getLogger(getClass());

    @Test
    void messages_from_network_should_be_forwarded_to_microservice() throws Throwable {
        /* Dependencies */
        ExecutorService executorService = Executors.newCachedThreadPool();
        RabbitBrokerConnecter rabbitBrokerConnecter = mock(RabbitBrokerConnecter.class);

        RetrySleeper retrySleeper = () -> {
            // Do not sleep at all.
        };

        InputFileOpener microserviceReaderOpener = mock(InputFileOpener.class);
        when(microserviceReaderOpener.openStream(any(RetrySleeper.class))).thenReturn(mock(InputFileReader.class));

        OutputFileOpener microserviceWriterOpener = mock(OutputFileOpener.class);
        OutputFileWriter microserviceWriter = mock(OutputFileWriter.class);
        when(microserviceWriterOpener.openStream(any(RetrySleeper.class))).thenReturn(microserviceWriter);

        BlockingRabbitConsumer blockingRabbitConsumer = mock(BlockingRabbitConsumer.class);

        doAnswer((invocation) -> {
            logger.info("Test messages are now being consumed.");
            RabbitMessageListener rabbitMessageListener = invocation.getArgument(2);
            rabbitMessageListener.messageReceived("Message 1 from network.");
            rabbitMessageListener.messageReceived("Message 2 from network.");
            return null;
        }).when(blockingRabbitConsumer).consume(isNull(), any(String.class), any(RabbitMessageListener.class));

        MsToNetworkForwarder msToNetworkForwarder = mock(MsToNetworkForwarder.class);

        // Init app
        App app = new App(
                executorService,
                rabbitBrokerConnecter,
                retrySleeper,
                microserviceReaderOpener,
                microserviceWriterOpener,
                new NetworkToMsForwarder(
                        NetworkMsgListenerFactoryFactory.create(),
                        blockingRabbitConsumer,
                        "someQueueName"
                ),
                msToNetworkForwarder
        );

        // Shutdownhook
        Shutdownhook shutdownhook = new Shutdownhook(app);

        ErrorHandlingTestRunner errorHandlingTestRunner = ErrorHandlingTestRunner.create();

        ExecutorService testExecutorService = Executors.newCachedThreadPool();

        // When
        Future test = testExecutorService.submit(() -> errorHandlingTestRunner.run(app));

        // Then
        test.get();
        shutdownhook.run(executorService);

        InOrder inOrder = Mockito.inOrder(microserviceWriter);
        inOrder.verify(microserviceWriter).write("Message 1 from network.");
        inOrder.verify(microserviceWriter).write("Message 2 from network.");
    }

    @Test
    void messages_from_microservice_should_be_forwarded_to_network() throws Throwable {
        /* Dependencies */
        ExecutorService executorService = Executors.newCachedThreadPool();
        RabbitBrokerConnecter rabbitBrokerConnecter = mock(RabbitBrokerConnecter.class);

        RetrySleeper retrySleeper = () -> {
            // Do not sleep at all.
        };

        InputFileOpener microserviceReaderOpener = mock(InputFileOpener.class);
        InputFileReader microserviceReader = mock(InputFileReader.class);
        when(microserviceReaderOpener.openStream(any(RetrySleeper.class))).thenReturn(microserviceReader);

        doAnswer((invocation) -> {
            MessageListener messageListener = invocation.getArgument(0);
            messageListener.messageReceived("Message 1 from microservice.");
            messageListener.messageReceived("Message 2 from microservice.");
            return null;
        }).when(microserviceReader).consume(any(MessageListener.class));

        OutputFileOpener microserviceWriterOpener = mock(OutputFileOpener.class);
        when(microserviceWriterOpener.openStream(any(RetrySleeper.class))).thenReturn(mock(OutputFileWriter.class));

        BlockingRabbitConsumer blockingRabbitConsumer = mock(BlockingRabbitConsumer.class);

        doAnswer((invocationOnMock) -> {
            // No messages from network.
            return null;
        }).when(blockingRabbitConsumer).consume(isNull(), any(String.class), any(RabbitMessageListener.class));

        RabbitPublisherFactory rabbitPublisherFactory = mock(RabbitPublisherFactory.class);
        RabbitPublisher rabbitPublisher = mock(RabbitPublisher.class);
        when(rabbitPublisherFactory.create(any())).thenReturn(rabbitPublisher);

        // Init app
        App app = new App(
                executorService,
                rabbitBrokerConnecter,
                retrySleeper,
                microserviceReaderOpener,
                microserviceWriterOpener,
                new NetworkToMsForwarder(
                        NetworkMsgListenerFactoryFactory.create(),
                        blockingRabbitConsumer,
                        "game"
                ),
                new MsToNetworkForwarder(
                        rabbitPublisherFactory,
                        "game"
                )
        );

        // Shutdownhook
        Shutdownhook shutdownhook = new Shutdownhook(app);

        ErrorHandlingTestRunner errorHandlingTestRunner = ErrorHandlingTestRunner.create();

        ExecutorService testExecutorService = Executors.newCachedThreadPool();

        // When
        Future test = testExecutorService.submit(() -> errorHandlingTestRunner.run(app));

        // Then
        test.get();
        shutdownhook.run(executorService);

        InOrder inOrder = Mockito.inOrder(rabbitPublisher);
        inOrder.verify(rabbitPublisher).publish("game", "Message 1 from microservice.");
        inOrder.verify(rabbitPublisher).publish("game", "Message 2 from microservice.");
    }
}