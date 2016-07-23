package com.yngvark.netcom;

public interface Connection {
    Topic subscribeTo(String topicName);
    void disconnect();
}
