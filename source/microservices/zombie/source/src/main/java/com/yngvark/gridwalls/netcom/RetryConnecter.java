package com.yngvark.gridwalls.netcom;

import com.yngvark.gridwalls.microservices.zombie.Config;
import com.yngvark.gridwalls.netcom.rabbitmq.BrokerConnecter;

public class RetryConnecter {
    private final Config config;
    private final BrokerConnecter brokerConnecter;

    private ConnectionWrapper connectionWrapper;

    public RetryConnecter(Config config, BrokerConnecter brokerConnecter) {
        this.config = config;
        this.brokerConnecter = brokerConnecter;
    }

    public ConnectAttempt tryToEnsureConnected() {
        int attemptCount = 3;
        ConnectAttempt connectAttempt;

        for (int i = 0; i < attemptCount; i++) {
            System.out.println("Connecting to " + config.getBrokerHostname() + " (attempt " + i + ")");
            connectAttempt = brokerConnecter.connect(config.getBrokerHostname(), 5000);

            if (connectAttempt.succeeded()) {
                System.out.println("Connected.");
                connectionWrapper = connectAttempt.getConnectionWrapper();
                return connectAttempt;
            } else {
                System.out.println("Cannot connect. Reason: " + connectAttempt.getConnectFailedDetails());
            }
        }

        return new ConnectFailed("Could not connect after " + attemptCount + " attempts.");
    }

    public void disconnectIfConnected() {
        if (connectionWrapper != null)
            connectionWrapper.disconnectIfConnected();
    }
}
