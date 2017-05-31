package com.yngvark.netcom;

import java.io.IOException;

class TestConnection implements Connection {

    @Override
    public void subscribeTo(String topicName) throws IOException {
        
    }

    @Override
    public Topic getSubscription(String topicName) {
        return null;
    }

    @Override
    public void publish(String topicName, String message) {

    }

    @Override
    public void disconnect() throws IOException {

    }
}
