package com.yngvark.gridwalls.micrfoservices.zombie_test;

class MapInfoRequest {
    private String replyToTopic;

    public MapInfoRequest replyToTopic(String replyToTopic) {
        this.replyToTopic = replyToTopic;
        return this;
    }
}
