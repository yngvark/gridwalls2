package com.yngvark.gridwalls.microservices.zombie;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.yngvark.gridwalls.netcom.NetCom;
import com.yngvark.gridwalls.netcom.rabbitmq.RabbitmqNetCom;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ModuleConfig extends AbstractModule {
    @Override
    protected void configure() {
        /*bind(CoordinateSerializer.class).to(CoordinateSerializer.class).in(Singleton.class);
        bind(ZombieMovedSerializer.class).to(ZombieMovedSerializer.class).in(Singleton.class);*/

        bind(CommandHandler.class).in(Singleton.class);
        bind(MessageBusConnecter.class).to(RabbitMqConnector.class);
        bind(NetCom.class).to(RabbitmqNetCom.class);
    }

    @Provides @Singleton
    public ExecutorService executorService() {
        return Executors.newCachedThreadPool();
    }

}
