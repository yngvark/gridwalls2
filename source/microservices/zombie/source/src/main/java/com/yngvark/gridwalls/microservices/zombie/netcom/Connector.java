package com.yngvark.gridwalls.microservices.zombie.netcom;

public interface Connector {
    ConnectAttempt connect(String host, int timeoutMilliseconds);
}
