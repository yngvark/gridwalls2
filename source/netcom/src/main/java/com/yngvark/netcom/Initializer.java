package com.yngvark.netcom;

public interface Initializer {
    void connect(String host);
    Connection getConnection();
}
