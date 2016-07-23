package com.yngvark.netcom;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class RabbitMqInitializerTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Initializer initializer;

    @Before
    public void before() {
        //initializer = new RabbitMqInitializer();
        initializer = new TestInitializer();
    }

    @Test
    public void should_throw_exception_if_no_host_is_provided() {
        expectedException.expect(NoHostProvidedException.class);
        initializer.connect("");
    }

    @Test
    public void should_connect_and_disconnect_without_error() {
        initializer.connect("rabbithost");
        Connection connection = initializer.getConnection();
        connection.disconnect();
    }

    @Test
    public void should_throw_error_if_attempt_to_get_connection_before_connected() {
        expectedException.expect(NotYetConnectedException.class);
        initializer.getConnection();
    }

    @Test
    public void should_publish_message_without_error() {
        // Given
        initializer.connect("rabbithost");
        Connection connection = initializer.getConnection();
        Topic clientOut = connection.subscribeTo("clientOut");

        // When
        clientOut.publish("Hello folks, this is a test in " + RabbitMqInitializer.class.getSimpleName());

        // Then no error should occur

        // Finally
        connection.disconnect();
    }

    @Test
    public void should_consume_published_message() {
        // Given
        initializer.connect("rabbithost");
        Connection connection = initializer.getConnection();
        Topic clientOut = connection.subscribeTo("clientOut");

        String outMsg = "Hello folks, this is a test in " + RabbitMqInitializer.class.getSimpleName();

        // When
        clientOut.publish(outMsg);

        // Then
        String consumedMSg = clientOut.consume();
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
