package com.yngvark.gridwalls.netcom;

public interface ConnectAttempt {
    boolean succeeded();
    boolean failed();
    String getConnectFailedDetails();
    ConnectionWrapper getConnectionWrapper();
}
