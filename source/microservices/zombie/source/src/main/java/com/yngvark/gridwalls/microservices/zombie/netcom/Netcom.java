package com.yngvark.gridwalls.microservices.zombie.netcom;

import com.yngvark.gridwalls.microservices.zombie.Config;

public class Netcom {
    private final Config config;
    private final Connector connector;

    public Netcom(Config config, Connector connector) {
        this.config = config;
        this.connector = connector;
    }

    public ConnectAttempt tryToEnsureConnected(ConnectionWrapper connection) {
        if (connection.isConnected())
            return new ConnectSucceeded(connection);

        return connect();
    }

    private ConnectAttempt connect() {
        int attemptCount = 3;
        ConnectAttempt connectAttempt;

        for (int i = 0; i < attemptCount; i++) {
            System.out.println("Connecting to " + config.RABBITMQ_HOST + " (attempt " + i + ")");

            connectAttempt = connector.connect(config.RABBITMQ_HOST, 5000);

            if (connectAttempt.succeeded()) {
                System.out.println("Connected.");
                return connectAttempt;
            } else {
                System.out.println("Cannot connect. Reason: " + connectAttempt.getConnectFailedDetails());
            }

        }

        return new ConnectFailed("Could not connect after " + attemptCount + " attempts.");
    }

}
