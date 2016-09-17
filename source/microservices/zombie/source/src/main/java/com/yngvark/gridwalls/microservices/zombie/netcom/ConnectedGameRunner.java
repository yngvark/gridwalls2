package com.yngvark.gridwalls.microservices.zombie.netcom;

import com.rabbitmq.client.Connection;

public class ConnectedGameRunner {
    private final Connection connection;

    public ConnectedGameRunner(Connection connection) {
        this.connection = connection;
    }


}
