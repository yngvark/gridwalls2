package com.yngvark.gridwalls.microservices.zombie;

public interface MessageBusConnecter {
    ConnectResult connect(int timeoutMilliseconds);
}
