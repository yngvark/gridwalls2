package com.yngvark.gridwalls.microservices.zombie.run_app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.yngvark.gridwalls.microservices.zombie.run_game.serialize_msgs.Serializer;

public class JsonSerializer implements Serializer {
    private final Gson gson = new GsonBuilder().create();

    @Override
    public String serialize(Object event) {
        return gson.toJson(event);
    }

    @Override
    public <T> String serialize(Object event, Class<T> clazz) {
        return gson.toJson(event, clazz);
    }

    @Override
    public <T> T deserialize(String msg, Class<T> clazz) {
        try {
            return gson.fromJson(msg, clazz);
        } catch (JsonSyntaxException e) {
            throw new RuntimeException("Could not deserialize: " + msg, e);
        }
    }
}
