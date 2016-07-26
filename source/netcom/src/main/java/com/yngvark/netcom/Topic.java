package com.yngvark.netcom;

public interface Topic {
    String consume();
    boolean hasMoreMessages();
}
