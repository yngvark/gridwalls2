package com.yngvark.gridwalls.microservices.zombie.gamelogic;

import com.rabbitmq.client.Connection;
import com.yngvark.gridwalls.microservices.zombie.infrastructure.ICanAbortOnSignal;

public class GameLoop implements ICanAbortOnSignal {
    public void run(Connection connection) {

    }

    public void startAborting() {

    }
}
