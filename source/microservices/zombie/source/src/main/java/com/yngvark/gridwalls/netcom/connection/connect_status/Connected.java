package com.yngvark.gridwalls.netcom.connection.connect_status;

import com.yngvark.gridwalls.netcom.connection.ConnectionWrapper;

public class Connected<T extends ConnectionWrapper> implements ConnectStatus<T> {
    private final T connectionWrapper;

    public Connected(T connectionWrapper) {
        this.connectionWrapper = connectionWrapper;
    }

    @Override
    public boolean connected() {
        return true;
    }

    @Override
    public boolean disconnected() {
        return false;
    }

    @Override
    public String getConnectFailedDetails() {
        return "";
    }

    @Override
    public T getConnectionWrapper() {
        return connectionWrapper;
    }
}
