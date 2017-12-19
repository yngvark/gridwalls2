package com.yngvark.gridwalls.microservices.zombie.react_to_events;

import com.google.gson.Gson;
import com.yngvark.gridwalls.microservices.zombie.move_zombie.Zombie;

public class EventHandler {
    private final Deserializer deserializer;
    private final Zombie zombie;

    public EventHandler(Deserializer deserializer, Zombie zombie) {
        this.deserializer = deserializer;
        this.zombie = zombie;
    }

    public static EventHandler create(Zombie zombie) {
        return new EventHandler(
                new Deserializer(new Gson()),
                zombie
        );
    }

    public void handle(String incomingEvent) {
    }
}
