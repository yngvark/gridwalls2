package com.yngvark.gridwalls.microservices.zombie;

import com.rabbitmq.client.Connection;

public interface ConnectResult {
    boolean isConnected();
    String getConnectFailedDetails();
    Connection getConnection();
}
