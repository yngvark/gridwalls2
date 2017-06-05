package com.yngvark.gridwalls.microservices.zombie.game;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

class JsonSerializer implements Serializer {
    private final Gson gson = new GsonBuilder().create();

    @Override
    public <T> String serialize(Object event, Class<T> clazz) {
        return gson.toJson(event, clazz);
    }

    @Override
    public <T> T deserialize(String msg, Class<T> clazz) {
        return gson.fromJson(msg, clazz);
    }
}
