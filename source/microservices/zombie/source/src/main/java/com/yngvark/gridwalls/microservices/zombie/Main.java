package com.yngvark.gridwalls.microservices.zombie;

import com.yngvark.gridwalls.core.CoordinateSerializer;
import com.yngvark.gridwalls.microservices.zombie.game.GameLoop;
import com.yngvark.gridwalls.microservices.zombie.game.GameRunner;
import com.yngvark.gridwalls.microservices.zombie.game.ZombieFactory;
import com.yngvark.gridwalls.microservices.zombie.game.ZombieMovedSerializer;
import com.yngvark.gridwalls.microservices.zombie.game.ZombiesController;
import com.yngvark.gridwalls.microservices.zombie.game.netcom.rabbitmq.RabbitPublisher;
import com.yngvark.gridwalls.microservices.zombie.game.os_process.ExecutorServiceExiter;
import com.yngvark.gridwalls.microservices.zombie.game.utils.GameErrorHandler;
import com.yngvark.gridwalls.microservices.zombie.game.os_process.ProcessRunner;
import com.yngvark.gridwalls.microservices.zombie.game.utils.StackTracePrinter;
import com.yngvark.gridwalls.netcom.publish.Publisher;
import com.yngvark.gridwalls.netcom.connection.RetryConnecter;
import com.yngvark.gridwalls.netcom.gameconfig.GameConfigDeserializer;
import com.yngvark.gridwalls.netcom.gameconfig.GameConfigFetcher;
import com.yngvark.gridwalls.netcom.Netcom;
import com.yngvark.gridwalls.microservices.zombie.game.netcom.ZombieMovedPublisher;
import com.yngvark.gridwalls.microservices.zombie.game.netcom.rabbitmq.RabbitBrokerConnecter;
import com.yngvark.gridwalls.microservices.zombie.game.netcom.rabbitmq.RabbitConnectionWrapper;
import com.yngvark.gridwalls.microservices.zombie.game.netcom.rabbitmq.RabbitRpcCaller;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Main {
    public static void main(String[] args) {
        System.out.println("Starting zombie 0.0.1.alpha");
        createGameRunner();
    }

    private static void createGameRunner() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        StackTracePrinter stackTracePrinter = new StackTracePrinter();

        Netcom<RabbitConnectionWrapper> netcom = new Netcom<>(
                new RetryConnecter<>(
                        Config.builder().brokerHostname("rabbithost").build(),
                        new RabbitBrokerConnecter(stackTracePrinter)
                ),
                new RabbitRpcCaller(),
                new RabbitPublisher()
        );

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
                                        new ZombieMovedPublisher(
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
