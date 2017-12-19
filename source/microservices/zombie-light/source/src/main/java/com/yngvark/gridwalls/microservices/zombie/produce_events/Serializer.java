package com.yngvark.gridwalls.microservices.zombie.produce_events;

import com.google.gson.Gson;

class Serializer {
    private final Gson gson;

    public Serializer(Gson gson) {
        this.gson = gson;
    }

    public String serialize(Object event) {
        return gson.toJson(event);
    }
}
