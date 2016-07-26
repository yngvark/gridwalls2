package com.yngvark.netcom;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

public class RabbitMqInitializerTest {
    private Initializer initializer;

    @BeforeEach
    public void before() {
        initializer = new RabbitMqInitializer();
        //initializer = new TestInitializer();
    }

    @Test
    public void should_throw_exception_if_no_host_is_provided() throws IOException, TimeoutException {
        expectThrows(NoHostProvidedException.class, () -> {
            initializer.connect("");
        });
    }

    @Test
    public void should_connect_and_disconnect_without_error() throws IOException, TimeoutException {
        initializer.connect("rabbithost");
        Connection connection = initializer.getConnection();
        connection.disconnect();
    }

    @Test
    public void should_throw_error_if_attempt_to_get_connection_before_connected() {
        expectThrows(NotYetConnectedException.class, () -> {
            initializer.getConnection();
        });
    }

    @Test
    public void should_publish_message_without_error() throws IOException, TimeoutException {
        // Given
        initializer.connect("rabbithost");
        Connection connection = initializer.getConnection();

        // When
        connection.publish("news", "Hello folks, this is a test in " + RabbitMqInitializer.class.getSimpleName());

        // Then no error should occur

        // Finally
        connection.disconnect();
    }

    @Test
    public void should_throw_error_if_attempt_to_get_subscription_before_subscribed_to_it() throws IOException, TimeoutException {
        initializer.connect("rabbithost");
        Connection connection = initializer.getConnection();

        expectThrows(NoSuchTopicException.class, () -> {
            connection.getSubscription("aTopicWeHaventSubscribedToYet");
        });

        connection.disconnect();
    }

    @Test
    @Disabled("Wait to implement until I can get Javadoc working.")
    public void should_consume_published_message() throws IOException, TimeoutException {
        // Given
        initializer.connect("rabbithost");
        Connection connection = initializer.getConnection();
        connection.subscribeTo("news");
        Topic news = connection.getSubscription("news");

        String outMsg = "Hello folks, this is a test in " + RabbitMqInitializer.class.getSimpleName();

        // When
        connection.publish("news", outMsg);

        // Then
        String consumedMSg = news.consume();
        assertEquals(outMsg, consumedMSg);

        // Finally
        connection.disconnect();
    }

//    @Test
//    public void should_consume_only_messages_from_specific_topic() {
//        // Given
//        initializer.connect("rabbithost");
//        Connection connection = initializer.getConnection();
//        connection.createOrGetTopic("")
//
//        String outMsg = "Hello folks, this is a test in " + RabbitMqInitializer.class.getSimpleName();
//
//        // When
//        connection.publish(outMsg, "clientOut");
//
//        // Then
//        assertFalse(connection.hasMoreMessages();
//
//        String consumedMSg = connection.consume("clientOut");
//        assertEquals(outMsg, consumedMSg);
//
//        // Finally
//        connection.disconnect();
//    }
}
