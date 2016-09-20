package com.yngvark.gridwalls.microservices.zombie.netcom;

import com.rabbitmq.client.Connection;

public interface ConnectResult {
    boolean success();
    String getConnectFailedDetails();
    Connection getConnection();
}
