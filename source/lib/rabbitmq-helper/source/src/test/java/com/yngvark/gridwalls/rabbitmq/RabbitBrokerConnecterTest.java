package com.yngvark.gridwalls.rabbitmq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RabbitBrokerConnecterTest {
    @Test
    void should_reconnect_if_host_doesnt_exist() throws IOException, TimeoutException {
        // Given
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        RabbitBrokerConnecter rabbitBrokerConnecter = new RabbitBrokerConnecter(
                "some_not_existing_host_a049fma094f",
                connectionFactory,
                (millis) -> { /* Don't sleep at all*/});

        when(connectionFactory.newConnection())
                .thenThrow(UnknownHostException.class)
                .thenReturn(mock(Connection.class));

        // When
        assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
            RabbitConnection rabbitConnection = rabbitBrokerConnecter.connect();
            assertNotNull(rabbitConnection);
        });

        // Then
        verify(connectionFactory);
    }

}