package com.yngvark.gridwalls.netcom.connection.connect_status;

import com.yngvark.gridwalls.netcom.connection.ConnectionWrapper;

public class Disconnected<T extends ConnectionWrapper> implements ConnectStatus<T> {
    private final String connectFailedDetails;

    public Disconnected(String connectFailedDetails) {
        this.connectFailedDetails = connectFailedDetails;
    }

    @Override
    public boolean connected() {
        return false;
    }

    @Override
    public boolean disconnected() {
        return true;
    }

    @Override
    public String getConnectFailedDetails() {
        return connectFailedDetails;
    }

    @Override
    public T getConnectionWrapper() {
        throw new RuntimeException("Cannot get the connection of a failed connection attempt.");
    }
}
