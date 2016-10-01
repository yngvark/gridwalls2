package com.yngvark.gridwalls.microservices.zombie.netcom;

public interface ConnectionWrapper<T> {
    boolean isConnected();
    T getConnection();
}
