package com.yngvark.gridwalls.microservices.zombie.game.netcom.rabbitmq;

import com.rabbitmq.client.Channel;
import com.yngvark.gridwalls.netcom.publish.PublishResult;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RabbitPublisherTest {
    @Test
    public void should_publish_to_channel() throws Exception {
        // Given
        RabbitPublisher rabbitPublisher = new RabbitPublisher();

        RabbitConnectionWrapper connectionWrapper = mock(RabbitConnectionWrapper.class);
        Channel channel = mock(Channel.class);
        when(connectionWrapper.getChannelForExchange("my_queue")).thenReturn(channel);

        // When
        PublishResult publishResult = rabbitPublisher.publish(connectionWrapper, "my_queue", "hello");

        // Then
        verify(channel).basicPublish(eq("my_queue"), eq(""), any(), eq("hello".getBytes()));
        assertTrue(publishResult.succeeded());
        assertTrue(publishResult.getFailedInfo().length() > 0);
    }

    @Test
    public void should_return_failure_info_when_channel_initialization_fails() throws Exception {
        // Given
        RabbitPublisher rabbitPublisher = new RabbitPublisher();

        RabbitConnectionWrapper connectionWrapper = mock(RabbitConnectionWrapper.class);

        doThrow(new IOException("ChannelFailure"))
                .when(connectionWrapper).getChannelForExchange(eq("my_queue"));

        // When
        PublishResult publishResult = rabbitPublisher.publish(connectionWrapper, "my_queue", "hello");

        // Then
        verify(connectionWrapper).getChannelForExchange(eq("my_queue"));
        assertFalse(publishResult.succeeded());
        assertEquals("Could not publish message, because channel initialization failure. Details: ChannelFailure", publishResult.getFailedInfo());
    }

    @Test
    public void should_return_failure_info_when_publish_fails() throws Exception {
        // Given
        RabbitPublisher rabbitPublisher = new RabbitPublisher();

        RabbitConnectionWrapper connectionWrapper = mock(RabbitConnectionWrapper.class);
        Channel channel = mock(Channel.class);
        when(connectionWrapper.getChannelForExchange("my_queue")).thenReturn(channel);

        doThrow(new IOException("Could not open connection."))
                .when(channel).basicPublish(eq("my_queue"), eq(""), any(), eq("hello".getBytes()));

        // When
        PublishResult publishResult = rabbitPublisher.publish(connectionWrapper, "my_queue", "hello");

        // Then
        verify(channel).basicPublish(eq("my_queue"), eq(""), any(), eq("hello".getBytes()));
        assertFalse(publishResult.succeeded());
        assertEquals("Could not publish message. Details: Could not open connection.", publishResult.getFailedInfo());
    }

}