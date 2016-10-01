package com.yngvark.gridwalls.microservices.zombie.netcom;

public interface ConnectionWrapper<T> {
    T getConnection();
}
