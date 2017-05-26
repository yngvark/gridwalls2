package com.yngvark.gridwalls.netcom;

import com.yngvark.gridwalls.netcom.connection.BrokerConnecterHolder;
import com.yngvark.gridwalls.netcom.connection.ConnectionWrapper;
import com.yngvark.gridwalls.netcom.connection.connect_status.Connected;
import com.yngvark.gridwalls.netcom.connection.connect_status.Disconnected;
import com.yngvark.gridwalls.netcom.consume.ConsumeHandler;
import com.yngvark.gridwalls.netcom.consume.Consumer;
import com.yngvark.gridwalls.netcom.publish.NetcomResult;
import com.yngvark.gridwalls.netcom.publish.NetcomSucceeded;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public class NetcomConsumeTest {
    @Test
    public void should_start_consuming_messages_when_connected() throws Exception {
        // Given
        BrokerConnecterHolder brokerConnecterHolder = mock(BrokerConnecterHolder.class);
        when(brokerConnecterHolder.connectIfNotConnected()).thenReturn(new Connected(mock(ConnectionWrapper.class)));

        Consumer consumer = mock(Consumer.class);
        ConsumeHandler consumeHandler = mock(ConsumeHandler.class);
        when(consumer.startConsume(any(ConnectionWrapper.class), eq("my_queue"), eq(consumeHandler))).thenReturn(new NetcomSucceeded());

        Netcom netcom = new Netcom(brokerConnecterHolder, null, null, consumer);

        // When
        NetcomResult startConsumeResult = netcom.startConsume("my_queue", consumeHandler);

        // Then
        verify(consumer).startConsume(any(ConnectionWrapper.class), eq("my_queue"), eq(consumeHandler));

        assertTrue(startConsumeResult.succeeded());
        assertTrue(startConsumeResult.getFailedInfo().length() > 0);
    }

    @Test
    public void should_return_error_when_unable_to_connect_when_starting_to_consume_messages() throws Exception {
        // Given
        BrokerConnecterHolder brokerConnecterHolder = mock(BrokerConnecterHolder.class);
        when(brokerConnecterHolder.connectIfNotConnected()).thenReturn(new Disconnected("Could not contact host."));

        Consumer consumer = mock(Consumer.class);
        Netcom netcom = new Netcom(brokerConnecterHolder, null, null, consumer);

        // When
        NetcomResult startConsumeResult = netcom.startConsume("my_queue", mock(ConsumeHandler.class));

        // Then
        verify(consumer, times(0)).startConsume(any(ConnectionWrapper.class), any(String.class), any(ConsumeHandler.class));


        assertFalse(startConsumeResult.succeeded());
        assertEquals("Could not consume. Details: Could not contact host.", startConsumeResult.getFailedInfo());
    }
}
