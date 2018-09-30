package com.yngvark.gridwalls.microservices.map_info_producer;

class MapInfoRequest {
    final String replyToTopic;

    public MapInfoRequest(String replyToTopic) {
        this.replyToTopic = replyToTopic;
    }
}
