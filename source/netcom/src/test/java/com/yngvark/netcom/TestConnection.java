package com.yngvark.netcom;

import java.util.Stack;

class TestConnection implements Connection {
    @Override
    public Topic subscribeTo(String topicName) {
        return new TestTopic();
    }

    @Override
    public void disconnect() {
    }

}
