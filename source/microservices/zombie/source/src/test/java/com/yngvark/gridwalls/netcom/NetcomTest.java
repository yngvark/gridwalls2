package com.yngvark.gridwalls.netcom;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.expectThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NetcomTest {
    @Test
    public void should_do_rpc_call_when_connected() throws Exception {
        // Given
        RetryConnecter retryConnecter = mock(RetryConnecter.class);
        when(retryConnecter.tryToEnsureConnected()).thenReturn(new Connected(mock(ConnectionWrapper.class)));

        RpcCaller rpcCaller = mock(RpcCaller.class);
        when(rpcCaller.rpcCall(any(ConnectionWrapper.class), eq("rpc_queue"), eq("hello"))).thenReturn(new RpcSucceeded("my RPC response"));

        Netcom netcom = new Netcom(retryConnecter, rpcCaller);

        // When
        RpcResult rpcResult = netcom.rpcCall("rpc_queue", "hello");

        // Then
        verify(rpcCaller).rpcCall(any(ConnectionWrapper.class), eq("rpc_queue"), eq("hello"));

        assertTrue(rpcResult.success());
        assertFalse(rpcResult.failed());
        assertTrue(rpcResult.getFailedInfo().length() > 0);
        assertEquals("my RPC response", rpcResult.getRpcResponse());
    }

    @Test
    public void should_return_error_when_unable_to_connect_when_doing_rpc_call() throws Exception {
        // Given
        RetryConnecter retryConnecter = mock(RetryConnecter.class);
        when(retryConnecter.tryToEnsureConnected()).thenReturn(new Disconnected("Could not contact host."));

        RpcCaller rpcCaller = mock(RpcCaller.class);
        when(rpcCaller.rpcCall(any(ConnectionWrapper.class), eq("rpc_queue"), eq("hello"))).thenReturn(new RpcSucceeded("my RPC response"));

        Netcom netcom = new Netcom(retryConnecter, rpcCaller);

        // When
        RpcResult rpcResult = netcom.rpcCall("rpc_queue", "hello");

        // Then
        verify(rpcCaller, times(0)).rpcCall(any(ConnectionWrapper.class), any(String.class), any(String.class));

        assertFalse(rpcResult.success());
        assertTrue(rpcResult.failed());
        assertEquals("Could not connect. Details: Could not contact host.", rpcResult.getFailedInfo());
        assertTrue(rpcResult.getRpcResponse().length() > 0);
    }

}