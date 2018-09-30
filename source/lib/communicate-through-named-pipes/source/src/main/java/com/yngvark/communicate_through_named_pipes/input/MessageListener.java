package com.yngvark.communicate_through_named_pipes.input;

public interface MessageListener {
    void messageReceived(String msg);
}
