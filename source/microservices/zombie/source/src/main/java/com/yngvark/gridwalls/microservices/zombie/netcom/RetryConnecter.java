package com.yngvark.gridwalls.microservices.zombie.netcom;

import com.yngvark.gridwalls.microservices.zombie.Config;

public class RetryConnecter {
    private final Config config;
    private final OneTImeConnecter oneTImeConnecter;
    private final ConnectFailedFactory connectFailedFactory;

    public RetryConnecter(Config config, OneTImeConnecter oneTImeConnecter,
            ConnectFailedFactory connectFailedFactory) {
        this.config = config;
        this.oneTImeConnecter = oneTImeConnecter;
        this.connectFailedFactory = connectFailedFactory;
    }

    public ConnectAttempt tryToEnsureConnected() {
        int attemptCount = 3;
        ConnectAttempt connectAttempt;

        for (int i = 0; i < attemptCount; i++) {
            System.out.println("Connecting to " + config.RABBITMQ_HOST + " (attempt " + i + ")");

            connectAttempt = oneTImeConnecter.connect(config.RABBITMQ_HOST, 5000);

            if (connectAttempt.succeeded()) {
                System.out.println("Connected.");
                return connectAttempt;
            } else {
                System.out.println("Cannot connect. Reason: " + connectAttempt.getConnectFailedDetails());
            }
        }

        return connectFailedFactory.failed("Could not connect after " + attemptCount + " attempts.");
    }
}
