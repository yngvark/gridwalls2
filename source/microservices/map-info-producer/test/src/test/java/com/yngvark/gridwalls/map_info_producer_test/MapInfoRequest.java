package com.yngvark.gridwalls.map_info_producer_test;

class MapInfoRequest {
    private String replyToTopic;

    public MapInfoRequest replyToTopic(String replyToTopic) {
        this.replyToTopic = replyToTopic;
        return this;
    }
}
