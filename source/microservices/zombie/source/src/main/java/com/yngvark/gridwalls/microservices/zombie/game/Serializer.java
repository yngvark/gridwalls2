package com.yngvark.gridwalls.microservices.zombie.game;

interface Serializer {
    <T> String serialize(Object event, Class<T> clazz);
    <T> T deserialize(String msg, Class<T> clazz);
}
