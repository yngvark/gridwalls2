package com.yngvark.gridwalls.netcom;

public interface ConnectStatus<T extends ConnectionWrapper> {
    boolean succeeded();
    boolean failed();
    String getConnectFailedDetails();
    T getConnectionWrapper();
}
