package com.yngvark.gridwalls.microservices.zombie.game.netcom.rabbitmq;

import com.rabbitmq.client.Channel;
import com.yngvark.gridwalls.netcom.publish.NetcomResult;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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
        when(connectionWrapper.getChannelForQueue("my_queue")).thenReturn(channel);

        // When
        NetcomResult netcomResult = rabbitPublisher.publish(connectionWrapper, "my_queue", "hello");

        // Then
        verify(channel).basicPublish(eq("my_queue"), eq(""), any(), eq("hello".getBytes()));
        assertTrue(netcomResult.succeeded());
        assertTrue(netcomResult.getFailedInfo().length() > 0);
    }

    @Test
    public void should_return_failure_info_when_channel_initialization_fails() throws Exception {
        // Given
        RabbitPublisher rabbitPublisher = new RabbitPublisher();

        RabbitConnectionWrapper connectionWrapper = mock(RabbitConnectionWrapper.class);

        doThrow(new IOException("ChannelFailure"))
                .when(connectionWrapper).getChannelForQueue(eq("my_queue"));

        // When
        NetcomResult netcomResult = rabbitPublisher.publish(connectionWrapper, "my_queue", "hello");

        // Then
        verify(connectionWrapper).getChannelForQueue(eq("my_queue"));
        assertFalse(netcomResult.succeeded());
        assertEquals("Could not publish message, because channel initialization failure. Details: ChannelFailure", netcomResult.getFailedInfo());
    }

    @Test
    public void should_return_failure_info_when_publish_fails() throws Exception {
        // Given
        RabbitPublisher rabbitPublisher = new RabbitPublisher();

        RabbitConnectionWrapper connectionWrapper = mock(RabbitConnectionWrapper.class);
        Channel channel = mock(Channel.class);
        when(connectionWrapper.getChannelForQueue("my_queue")).thenReturn(channel);

        doThrow(new IOException("Error when sending output."))
                .when(channel).basicPublish(eq("my_queue"), eq(""), any(), eq("hello".getBytes()));

        // When
        NetcomResult netcomResult = rabbitPublisher.publish(connectionWrapper, "my_queue", "hello");

        // Then
        verify(channel).basicPublish(eq("my_queue"), eq(""), any(), eq("hello".getBytes()));
        assertFalse(netcomResult.succeeded());
        assertEquals("Could not publish message, because publish failure. Details: Error when sending output.", netcomResult.getFailedInfo());
    }

    @Test
    @Ignore
    public void test_time_format() {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("kk:mm:ss.SSS"));
        System.out.println(time);
    }

}