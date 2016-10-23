package com.yngvark.gridwalls.netcom;

import com.yngvark.gridwalls.microservices.zombie.Config;
import com.yngvark.gridwalls.netcom.rabbitmq.BrokerConnecter;

public class RetryConnecter<T extends ConnectionWrapper> {
    private final Config config;
    private final BrokerConnecter<T> brokerConnecter;

    private T connectionWrapper;

    public RetryConnecter(Config config, BrokerConnecter<T> brokerConnecter) {
        this.config = config;
        this.brokerConnecter = brokerConnecter;
    }

    public ConnectStatus<T> tryToEnsureConnected() {
        int attemptCount = 3;
        ConnectStatus<T> connectStatus;

        for (int i = 0; i < attemptCount; i++) {
            System.out.println("Connecting to " + config.getBrokerHostname() + " (attempt " + i + ")");
            connectStatus = brokerConnecter.connect(config.getBrokerHostname(), 5000);

            if (connectStatus.succeeded()) {
                System.out.println("Connected.");
                connectionWrapper = connectStatus.getConnectionWrapper();
                return connectStatus;
            } else {
                System.out.println("Cannot connect. Reason: " + connectStatus.getConnectFailedDetails());
            }
        }

        return new Disconnected<>("Could not connect after " + attemptCount + " attempts.");
    }

    public void disconnectIfConnected() {
        if (connectionWrapper != null)
            connectionWrapper.disconnectIfConnected();
    }
}
