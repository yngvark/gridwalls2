package com.yngvark.gridwalls.microservices.zombie;

import com.yngvark.gridwalls.core.CoordinateSerializer;
import com.yngvark.gridwalls.microservices.zombie.gamelogic.GameLoop;
import com.yngvark.gridwalls.microservices.zombie.gamelogic.GameRunner;
import com.yngvark.gridwalls.microservices.zombie.gamelogic.ZombieFactory;
import com.yngvark.gridwalls.microservices.zombie.gamelogic.ZombieMovedSerializer;
import com.yngvark.gridwalls.microservices.zombie.gamelogic.ZombiesController;
import com.yngvark.gridwalls.microservices.zombie.infrastructure.ExecutorServiceExiter;
import com.yngvark.gridwalls.microservices.zombie.infrastructure.GameErrorHandler;
import com.yngvark.gridwalls.microservices.zombie.infrastructure.ProcessRunner;
import com.yngvark.gridwalls.microservices.zombie.infrastructure.StackTracePrinter;
import com.yngvark.gridwalls.netcom.GameConfigDeserializer;
import com.yngvark.gridwalls.netcom.GameConfigFetcher;
import com.yngvark.gridwalls.netcom.Netcom;
import com.yngvark.gridwalls.netcom.NetcomBuilder;
import com.yngvark.gridwalls.netcom.Publisher;
import com.yngvark.gridwalls.netcom.rabbitmq.RabbitBrokerConnecter;
import com.yngvark.gridwalls.netcom.rabbitmq.RabbitConnectionWrapper;
import com.yngvark.gridwalls.netcom.rabbitmq.RabbitRpcCaller;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting zombie 0.0.1.alpha");
        createGameRunner();
    }

    private static void createGameRunner() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        StackTracePrinter stackTracePrinter = new StackTracePrinter();

        Netcom<RabbitConnectionWrapper> netcom = new NetcomBuilder<RabbitConnectionWrapper>()
                .setBrokerHostname("rabbithost")
                .setBrokerConnecter(new RabbitBrokerConnecter(stackTracePrinter))
                .setRpcCaller(new RabbitRpcCaller())
                .create();

        ProcessRunner processRunner = new ProcessRunner(
                executorService,
                new ExecutorServiceExiter(stackTracePrinter),
                new GameRunner(
                        new GameConfigFetcher(
                                executorService,
                                stackTracePrinter,
                                netcom,
                                new GameConfigDeserializer()
                        ),
                        new GameLoop(
                                new ZombiesController(
                                        new ZombieFactory(),
                                        new Publisher(
                                                new ZombieMovedSerializer(new CoordinateSerializer()),
                                                netcom)
                                ),
                                new GameErrorHandler())
                ),
                netcom
        );

        processRunner.run();
    }

}
