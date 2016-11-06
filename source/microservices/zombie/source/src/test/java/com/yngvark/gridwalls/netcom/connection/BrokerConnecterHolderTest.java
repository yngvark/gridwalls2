package com.yngvark.gridwalls.netcom.connection;

import com.yngvark.gridwalls.microservices.zombie.Config;
import com.yngvark.gridwalls.netcom.connection.connect_status.ConnectionStatus;
import com.yngvark.gridwalls.netcom.connection.connect_status.Connected;
import com.yngvark.gridwalls.netcom.connection.connect_status.Disconnected;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BrokerConnecterHolderTest {
    // TEST: Retryconnecter skal ikke retrye når den allerede er connecta. Må huske at den er det.
    // Trenger i så fall en måte å si til retryconnecter at nå er den disconnecta.

    @Test
    public void should_connect_if_not_connected() { // TODO: Put this in integration test instead...?
        // Given
        BrokerConnecter brokerConnecter = mock(BrokerConnecter.class);
        when(brokerConnecter.connect(eq("someHost"), any(Integer.class))).thenReturn(connected());

        BrokerConnecterHolder brokerConnecterHolder = new BrokerConnecterHolder(
                Config.builder().brokerHostname("someHost").build(),
                brokerConnecter
                );

        // When
        ConnectionStatus<TestConnectionWrapper> connectionStatus = brokerConnecterHolder.connectIfNotConnected();

        // Then
        assertTrue(connectionStatus.connected());
        verify(brokerConnecter).connect(eq("someHost"), any(Integer.class));

        // And given
        Mockito.reset(brokerConnecter);
        when(brokerConnecter.connect(eq("someHost"), any(Integer.class))).thenReturn(connected());

        // When
        connectionStatus = brokerConnecterHolder.connectIfNotConnected();

        // Then
        verify(brokerConnecter, times(0)).connect(any(String.class), any(Integer.class));

        assertTrue(connectionStatus.connected());
    }

    private ConnectionStatus connected() {
        ConnectionWrapper connectionWrapperToReturn = new TestConnectionWrapper();
        return new Connected(connectionWrapperToReturn);
    }

    class TestConnectionWrapper implements ConnectionWrapper {
        @Override
        public void disconnectIfConnected() { }
    }

    @Test
    public void should_not_reconnect_if_reconnect_disabled() {
        // Given
        BrokerConnecter brokerConnecter = mock(BrokerConnecter.class);

        BrokerConnecterHolder brokerConnecterHolder = new BrokerConnecterHolder(
                Config.builder().brokerHostname("someHost").build(),
                brokerConnecter
        );

        brokerConnecterHolder.disconnectAndDisableReconnect();

        // When
        ConnectionStatus<TestConnectionWrapper> connectionStatus = brokerConnecterHolder.connectIfNotConnected();

        // Then
        verify(brokerConnecter, times(0)).connect(any(String.class), any(Integer.class));
        assertTrue(connectionStatus.disconnected());
        assertEquals("(Re)Connect disabled, won't connect.", connectionStatus.getConnectFailedDetails());
    }

}