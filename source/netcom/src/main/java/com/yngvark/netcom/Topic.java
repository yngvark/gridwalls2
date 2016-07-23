package com.yngvark.netcom;

public interface Topic {
    void publish(String event);
    String consume();
    boolean hasMoreMessages();
}
