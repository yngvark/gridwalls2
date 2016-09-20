package com.yngvark.gridwalls.microservices.zombie.netcom;

import java.util.concurrent.TimeUnit;

public interface BrokerConnecter {
    ConnectResult connect(String host, int timeoutMilliseconds);
    ConnectResult connect(String host, int port, int timeoutMilliseconds);
}
