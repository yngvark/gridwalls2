package com.yngvark.gridwalls.microservices.zombie.react_to_events;

import com.google.gson.Gson;
import com.yngvark.gridwalls.microservices.zombie.common.Serializer;
import com.yngvark.gridwalls.microservices.zombie.move_zombie.Zombie;

public class EventHandler {
    private final Serializer serializer;
    private final Zombie zombie;

    public EventHandler(Serializer serializer, Zombie zombie) {
        this.serializer = serializer;
        this.zombie = zombie;
    }

    public static EventHandler create(Zombie zombie) {
        return new EventHandler(
                new Serializer(new Gson()),
                zombie
        );
    }

    public void handle(String incomingEvent) {
    }
}
