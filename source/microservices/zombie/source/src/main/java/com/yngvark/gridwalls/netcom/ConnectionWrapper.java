package com.yngvark.gridwalls.netcom;

public interface ConnectionWrapper<T> {
    T getConnection();
    void disconnectIfConnected();
}
