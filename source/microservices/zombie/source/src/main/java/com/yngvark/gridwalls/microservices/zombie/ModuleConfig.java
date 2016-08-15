package com.yngvark.gridwalls.microservices.zombie;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class ModuleConfig extends AbstractModule {
    @Override
    protected void configure() {
        bind(ZombieMovedSerializer.class).to(ZombieMovedSerializer.class).in(Singleton.class);
    }
}
