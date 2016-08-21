package com.yngvark.gridwalls.microservices.zombie;

import com.google.inject.Guice;
import com.google.inject.Injector;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Main2 {
    public static void main(String[] argv) throws IOException, TimeoutException, InterruptedException {
        Injector injector = Guice.createInjector(new ModuleConfig());
        GameRunner2 gameRunner2 = injector.getInstance(GameRunner2.class);
        gameRunner2.run();
    }
}
