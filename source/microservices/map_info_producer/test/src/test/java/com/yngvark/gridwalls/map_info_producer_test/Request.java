package com.yngvark.gridwalls.map_info_producer_test;

class Request {
    private String replyToTopic;

    public Request replyToTopic(String replyToTopic) {
        this.replyToTopic = replyToTopic;
        return this;
    }
}
