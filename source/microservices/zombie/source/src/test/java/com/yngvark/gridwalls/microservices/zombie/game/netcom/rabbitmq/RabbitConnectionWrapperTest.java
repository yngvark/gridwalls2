package com.yngvark.gridwalls.microservices.zombie.game.netcom.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RabbitConnectionWrapperTest {
    @Test
    public void should_initialize_channel_with_correct_settings() throws Exception {
        // Given
        Connection connection = mock(Connection.class);
        Channel channelMock = mock(Channel.class);
        when(connection.createChannel()).thenReturn(channelMock);

        RabbitConnectionWrapper rabbitConnectionWrapper = new RabbitConnectionWrapper(null, connection);

        // When
        Channel channel = rabbitConnectionWrapper.getChannelForExchange("my_queue");

        // Then
        assertSame(channelMock, channel);
        verify(channelMock).exchangeDeclare(eq("my_queue"), eq("fanout"), eq(false), eq(true), any());
    }

}