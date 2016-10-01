package com.yngvark.gridwalls.netcom;

import com.yngvark.gridwalls.microservices.zombie.utils.MessageFormatter;

public class ConnectFailed implements ConnectAttempt {
    private final MessageFormatter messageFormatter;

    private String connectFailedDetails;

    public ConnectFailed(MessageFormatter messageFormatter, String connectFailedDetails) {
        this.messageFormatter = messageFormatter;
        this.connectFailedDetails = connectFailedDetails;
    }

    @Override
    public boolean succeeded() {
        return false;
    }

    @Override
    public boolean failed() {
        return true;
    }

    @Override
    public String getConnectFailedDetails() {
        return connectFailedDetails;
    }

    @Override
    public ConnectionWrapper getConnectionWrapper() {
        throw new RuntimeException("Cannot get the connection of a failed connection attempt.");
    }
}
