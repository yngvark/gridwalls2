package com.yngvark.gridwalls.microservices.zombie;

import com.yngvark.gridwalls.core.CoordinateSerializer;
import com.yngvark.gridwalls.microservices.zombie.game.GameLoopFactory;
import com.yngvark.gridwalls.microservices.zombie.game.GameRunner;
import com.yngvark.gridwalls.microservices.zombie.game.GameLoopRunner;
import com.yngvark.gridwalls.microservices.zombie.game.GameCleanup;
import com.yngvark.gridwalls.microservices.zombie.game.ServerMessagesConsumer;
import com.yngvark.gridwalls.microservices.zombie.game.ShutdownFromServerHandler;
import com.yngvark.gridwalls.microservices.zombie.game.ZombieFactory;
import com.yngvark.gridwalls.microservices.zombie.game.ZombieMovedSerializer;
import com.yngvark.gridwalls.microservices.zombie.game.ZombiesController;
import com.yngvark.gridwalls.microservices.zombie.game.netcom.ZombieMovedPublisher;
import com.yngvark.gridwalls.microservices.zombie.game.netcom.rabbitmq.RabbitBrokerConnecter;
import com.yngvark.gridwalls.microservices.zombie.game.netcom.rabbitmq.RabbitConnectionWrapper;
import com.yngvark.gridwalls.microservices.zombie.game.netcom.rabbitmq.RabbitConsumer;
import com.yngvark.gridwalls.microservices.zombie.game.netcom.rabbitmq.RabbitPublisher;
import com.yngvark.gridwalls.microservices.zombie.game.netcom.rabbitmq.RabbitRpcCaller;
import com.yngvark.gridwalls.microservices.zombie.game.os_process.ExecutorServiceExiter;
import com.yngvark.gridwalls.microservices.zombie.game.os_process.ProcessRunner;
import com.yngvark.gridwalls.microservices.zombie.game.os_process.ShutdownHook;
import com.yngvark.gridwalls.microservices.zombie.game.utils.GameErrorHandler;
import com.yngvark.gridwalls.microservices.zombie.game.utils.StackTracePrinter;
import com.yngvark.gridwalls.netcom.Netcom;
import com.yngvark.gridwalls.netcom.connection.BrokerConnecterHolder;
import com.yngvark.gridwalls.netcom.gameconfig.Deserializer;
import com.yngvark.gridwalls.netcom.gameconfig.GameConfigFetcher;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

class Main {
    public static void main(String[] args) {
        System.out.println("Starting zombie 0.0.1.alpha");
        createGameRunner();
    }

    private static void createGameRunner() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        StackTracePrinter stackTracePrinter = new StackTracePrinter();

        Netcom<RabbitConnectionWrapper> netcom = new Netcom<>(
                new BrokerConnecterHolder<>(
                        Config.builder().brokerHostname("rabbithost").build(),
                        new RabbitBrokerConnecter(stackTracePrinter)
                ),
                new RabbitRpcCaller(),
                new RabbitPublisher(),
                new RabbitConsumer()
        );
        GameRunner gameRunner = new GameRunner(
                new GameConfigFetcher(
                        executorService,
                        netcom,
                        new Deserializer()
                ),
                new GameLoopRunner(
                        new GameLoopFactory(
                                new ZombiesController(
                                        new ZombieFactory(),
                                        new ZombieMovedPublisher(
                                                new ZombieMovedSerializer(new CoordinateSerializer()),
                                                netcom)
                                ),
                                new GameErrorHandler()
                        ),
                        new LinkedBlockingQueue<>()
                )
        );
        GameCleanup gameCleanup = new GameCleanup(
                netcom,
                new ExecutorServiceExiter(executorService, stackTracePrinter)
        );
        ShutdownHook shutdownHook = new ShutdownHook(gameRunner, gameCleanup
        );
        ProcessRunner processRunner = new ProcessRunner(
                shutdownHook,
                new ServerMessagesConsumer(
                        netcom,
                        new ShutdownFromServerHandler(shutdownHook)
                ),
                gameRunner,
                gameCleanup
        );

        processRunner.run();
    }

}
