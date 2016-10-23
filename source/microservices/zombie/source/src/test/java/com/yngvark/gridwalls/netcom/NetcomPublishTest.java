package com.yngvark.gridwalls.netcom;

import com.yngvark.gridwalls.netcom.connection.ConnectionWrapper;
import com.yngvark.gridwalls.netcom.connection.RetryConnecter;
import com.yngvark.gridwalls.netcom.connection.connect_status.Connected;
import com.yngvark.gridwalls.netcom.connection.connect_status.Disconnected;
import com.yngvark.gridwalls.netcom.publish.PublishResult;
import com.yngvark.gridwalls.netcom.publish.PublishSucceeded;
import com.yngvark.gridwalls.netcom.publish.Publisher;
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
public class NetcomPublishTest {
    @Test
    public void should_publish_message_when_connected() throws Exception {
        // Given
        RetryConnecter retryConnecter = mock(RetryConnecter.class);
        when(retryConnecter.tryToEnsureConnected()).thenReturn(new Connected(mock(ConnectionWrapper.class)));

        Publisher publisher = mock(Publisher.class);
        ConnectionWrapper connectionWrapper = mock(ConnectionWrapper.class);
        when(publisher.publish(eq(connectionWrapper), eq("weather"), eq("it's raining"))).thenReturn(new PublishSucceeded());

        Netcom netcom = new Netcom(retryConnecter, null, publisher);

        // When
        PublishResult publishResult = netcom.publish("weather", "it's raining");

        // Then
        verify(publisher).publish(connectionWrapper, eq("weather"), eq("it's raining"));
        assertTrue(publishResult.succeeded());
        assertTrue(publishResult.getFailedInfo().length() > 0);
    }

    @Test
    public void should_return_error_when_unable_to_connect_when_publishing_message() throws Exception {
        // Given
        RetryConnecter retryConnecter = mock(RetryConnecter.class);
        when(retryConnecter.tryToEnsureConnected()).thenReturn(new Disconnected("Could not contact host."));

        Publisher publisher = mock(Publisher.class);

        Netcom netcom = new Netcom(retryConnecter, null, publisher);

        // When
        PublishResult publishResult = netcom.publish("weather", "it's raining");

        // Then
        verify(publisher, times(0)).publish(any(ConnectionWrapper.class), any(String.class), any(String.class));

        assertFalse(publishResult.succeeded());
        assertEquals("Could not publish. Details: Could not contact host.", publishResult.getFailedInfo());
    }
}
