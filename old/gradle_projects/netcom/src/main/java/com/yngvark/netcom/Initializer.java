package com.yngvark.netcom;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface Initializer {
    void connect(String host) throws IOException, TimeoutException;
    Connection getConnection();
}
