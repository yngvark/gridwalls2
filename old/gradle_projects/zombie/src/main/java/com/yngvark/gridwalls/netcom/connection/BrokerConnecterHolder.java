package com.yngvark.gridwalls.netcom.connection;

import com.yngvark.gridwalls.microservices.zombie.Config;
import com.yngvark.gridwalls.netcom.connection.connect_status.Connected;
import com.yngvark.gridwalls.netcom.connection.connect_status.ConnectionStatus;
import com.yngvark.gridwalls.netcom.connection.connect_status.Disconnected;

public class BrokerConnecterHolder<T extends ConnectionWrapper> {
    private final Config config;
    private final BrokerConnecter<T> brokerConnecter;

    private T connectionWrapper;
    private boolean isConnected = false;
    private boolean disconnectAndDisableReconnect = false;

    public BrokerConnecterHolder(Config config, BrokerConnecter<T> brokerConnecter) {
        this.config = config;
        this.brokerConnecter = brokerConnecter;
    }

    public ConnectionStatus<T> connectIfNotConnected() {
        if (isConnected)
            return new Connected<>(connectionWrapper);

        if (disconnectAndDisableReconnect)
            return new Disconnected<>("(Re)Connect disabled, won't connect.");

        int attemptCount = 3;
        ConnectionStatus<T> connectionStatus;

        for (int i = 0; i < attemptCount; i++) {
            System.out.println("Connecting to " + config.getBrokerHostname() + " (attempt " + i + ")");
            connectionStatus = brokerConnecter.connect(config.getBrokerHostname(), 5000);

            if (connectionStatus.connected()) {
                System.out.println("Connected.");
                connectionWrapper = connectionStatus.getConnectionWrapper();
                isConnected = true;
                return connectionStatus;
            } else {
                isConnected = false;
                System.out.println("Cannot connect. Retrying. Current connect failure reason: " + connectionStatus.getConnectFailedDetails());
            }
        }

        return new Disconnected<>("Could not connect after " + attemptCount + " attempts.");
    }

    public void disconnectAndDisableReconnect() {
        disconnectAndDisableReconnect = true;

        if (connectionWrapper != null)
            connectionWrapper.disconnectIfConnected();
    }
}
