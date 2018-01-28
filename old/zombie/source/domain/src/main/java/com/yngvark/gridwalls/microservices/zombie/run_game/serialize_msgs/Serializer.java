package com.yngvark.gridwalls.microservices.zombie.run_game.serialize_msgs;

public interface Serializer {
    <T> String serialize(Object event);
    <T> String serialize(Object event, Class<T> clazz);
    <T> T deserialize(String msg, Class<T> clazz);
}
