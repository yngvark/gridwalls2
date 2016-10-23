package com.yngvark.gridwalls.netcom;

import com.yngvark.gridwalls.netcom.connection.ConnectionWrapper;
import com.yngvark.gridwalls.netcom.connection.RetryConnecter;
import com.yngvark.gridwalls.netcom.connection.connect_status.Connected;
import com.yngvark.gridwalls.netcom.publish.PublishResult;
import com.yngvark.gridwalls.netcom.publish.PublishSucceeded;
import com.yngvark.gridwalls.netcom.publish.Publisher;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
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
        when(publisher.publish(eq("weather"), eq("it's raining"))).thenReturn(new PublishSucceeded());

        Netcom netcom = new Netcom(retryConnecter, null, publisher);

        // When
        PublishResult publishResult = netcom.publish("weather", "it's raining");

        // Then
        verify(publisher).publish(eq("weather"), eq("it's raining"));
        assertTrue(publishResult.succeeded());
    }
//
//    @Test
//    public void should_throw errof_when_disconnected() throws Exception {
//        // Given
//        RetryConnecter retryConnecter = mock(RetryConnecter.class);
//        when(retryConnecter.tryToEnsureConnected()).thenReturn(new Disconnected("Could not contact host."));
//
//        RpcCaller rpcCaller = mock(RpcCaller.class);
//        when(rpcCaller.rpcCall(any(ConnectionWrapper.class), eq("rpc_queue"), eq("hello"))).thenReturn(new RpcSucceeded("my RPC response"));
//
//        Publisher publisher = mock(Publisher.class);
//
//        Netcom netcom = new Netcom(retryConnecter, rpcCaller, publisher);
//
//        // When
//        PublishResult publishResult = netcom.publish("weather", "it's raining");
//
//        // Then
//        verify(publisher).publish(eq("weather"), eq("it's raining"));
//    }
}
