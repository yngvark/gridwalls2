package com.yngvark.gridwalls.microservices.zombie.netcom;

public interface ConnectAttempt<T> {
    boolean succeeded();
    boolean failed();
    String getConnectFailedDetails();
    ConnectionWrapper<T> getConnection();
}
