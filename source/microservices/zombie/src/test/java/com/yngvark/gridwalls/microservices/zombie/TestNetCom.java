package com.yngvark.gridwalls.microservices.zombie;

import com.yngvark.gridwalls.netcom.NetCom;

import java.util.Stack;

public class TestNetCom implements NetCom {
    private Stack<String> stack = new Stack<>();
    private Object lastEvent;

    @Override
    public void publish(String event) {
        stack.push(event);
    }

    public String consume() {
        String event = stack.pop();
        return event;
    }

    public boolean hasMoreEvents() {
        return stack.size() > 0;
    }
}
