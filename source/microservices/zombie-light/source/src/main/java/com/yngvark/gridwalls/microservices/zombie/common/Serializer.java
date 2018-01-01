package com.yngvark.gridwalls.microservices.zombie.common;

import com.google.gson.Gson;

public class Serializer {
    private final Gson gson;

    public Serializer(Gson gson) {
        this.gson = gson;
    }

    public String serialize(Object event) {
        return gson.toJson(event);
    }

    public <T> T deserialize(String data, Class<T> clazz) {
        return gson.fromJson(data, clazz);
    }
}
