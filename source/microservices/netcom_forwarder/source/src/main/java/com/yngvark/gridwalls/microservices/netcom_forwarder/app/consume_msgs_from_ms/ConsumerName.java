package com.yngvark.gridwalls.microservices.netcom_forwarder.app.consume_msgs_from_ms;

class ConsumerName {
    private boolean empty = true;
    private String consumerName = "(empty)";


    public boolean isEmpty() {
        return empty;
    }

    public void set(String consumerName) {
        this.consumerName = consumerName;
        empty = false;
    }

    public String get() {
        return consumerName;
    }
}
