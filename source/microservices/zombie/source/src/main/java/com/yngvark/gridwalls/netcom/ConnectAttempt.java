package com.yngvark.gridwalls.netcom;

public interface ConnectAttempt<T> {
    boolean succeeded();
    boolean failed();
    String getConnectFailedDetails();
    ConnectionWrapper<T> getConnectionWrapper();
}
