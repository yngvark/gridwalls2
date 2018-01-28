package com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs.get_map_info;

class MapInfoRequest {
    private String replyToTopic;

    public MapInfoRequest replyToTopic(String replyToTopic) {
        this.replyToTopic = replyToTopic;
        return this;
    }
}
