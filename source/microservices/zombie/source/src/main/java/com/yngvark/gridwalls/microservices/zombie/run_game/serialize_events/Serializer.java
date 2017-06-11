package com.yngvark.gridwalls.microservices.zombie.run_game.serialize_events;

public interface Serializer {
    <T> String serialize(Object event);
    <T> String serialize(Object event, Class<T> clazz);
    <T> T deserialize(String msg, Class<T> clazz);
}
