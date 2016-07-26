package com.yngvark.netcom;

import java.util.Stack;

public class TestTopic implements Topic {
    private Stack<String> stack = new Stack<>();

    public String consume() {
        if (!hasMoreMessages()) {
            throw new RuntimeException("There are no more events to consume.");
        }

        return stack.pop();
    }

    public boolean hasMoreMessages() {
        return stack.size() > 0;
    }
}
