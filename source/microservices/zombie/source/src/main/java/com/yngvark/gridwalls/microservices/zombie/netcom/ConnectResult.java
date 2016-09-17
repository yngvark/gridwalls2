package com.yngvark.gridwalls.microservices.zombie.netcom;

import com.rabbitmq.client.Connection;

public interface ConnectResult {
    boolean isConnected();
    String getConnectFailedDetails();
    Connection getConnection();
}
