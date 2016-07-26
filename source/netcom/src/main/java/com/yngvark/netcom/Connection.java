package com.yngvark.netcom;

import java.io.IOException;

public interface Connection {
    void subscribeTo(String topicName) throws IOException;

    Topic getSubscription(String topicName);

    void publish(String topicName, String message);

    void disconnect() throws IOException;
}
