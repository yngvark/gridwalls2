package com.yngvark.gridwalls.netcom;

import com.yngvark.gridwalls.microservices.zombie.Config;
import com.yngvark.gridwalls.netcom.rabbitmq.OneTimeConnecter;

import java.util.Optional;

public class RetryConnecter {
    private final Config config;
    private final OneTimeConnecter oneTimeConnecter;
    private final ConnectFailedFactory connectFailedFactory;

    private Optional<ConnectionWrapper> connectionWrapperOptional = Optional.empty();

    public RetryConnecter(Config config, OneTimeConnecter oneTimeConnecter,
            ConnectFailedFactory connectFailedFactory) {
        this.config = config;
        this.oneTimeConnecter = oneTimeConnecter;
        this.connectFailedFactory = connectFailedFactory;
    }

    public ConnectAttempt tryToEnsureConnected() {
        int attemptCount = 3;
        ConnectAttempt connectAttempt;

        for (int i = 0; i < attemptCount; i++) {
            System.out.println("Connecting to " + config.RABBITMQ_HOST + " (attempt " + i + ")");
            connectAttempt = oneTimeConnecter.connect(config.RABBITMQ_HOST, 5000);

            if (connectAttempt.succeeded()) {
                System.out.println("Connected.");
                connectionWrapperOptional = Optional.of(connectAttempt.getConnectionWrapper());
                return connectAttempt;
            } else {
                System.out.println("Cannot connect. Reason: " + connectAttempt.getConnectFailedDetails());
            }
        }

        return connectFailedFactory.failed("Could not connect after " + attemptCount + " attempts.");
    }

    public void disconnectIfConnected() {
        connectionWrapperOptional.ifPresent((connectionWrapper) -> connectionWrapper.disconnectIfConnected());
    }
}
