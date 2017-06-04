package com.yngvark.gridwalls.microservices.netcom_forwarder.app.consume_msgs_from_ms;

class Subscription {
    final String consumerName;
    final String exchange;

    public Subscription(String consumerName, String exchange) {
        this.consumerName = consumerName;
        this.exchange = exchange;
    }
}