package com.yngvark.gridwalls.microservices.zombie;

import com.yngvark.gridwalls.microservices.zombie.gamelogic.GameLoop;
import com.yngvark.gridwalls.microservices.zombie.gamelogic.GameRunner;
import com.yngvark.gridwalls.microservices.zombie.infrastructure.ExecutorServiceExiter;
import com.yngvark.gridwalls.microservices.zombie.infrastructure.ProcessRunner;
import com.yngvark.gridwalls.microservices.zombie.infrastructure.StackTracePrinter;
import com.yngvark.gridwalls.microservices.zombie.utils.MessageFormatter;
import com.yngvark.gridwalls.netcom.ConnectFailedFactory;
import com.yngvark.gridwalls.netcom.GameConfigDeserializer;
import com.yngvark.gridwalls.netcom.GameConfigFetcher;
import com.yngvark.gridwalls.netcom.Netcom;
import com.yngvark.gridwalls.netcom.RetryConnecter;
import com.yngvark.gridwalls.netcom.rabbitmq.RabbitOneTimeConnecter;
import com.yngvark.gridwalls.netcom.rabbitmq.RabbitRpcCaller;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        createGameRunner();
    }

    private static void createGameRunner() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        StackTracePrinter stackTracePrinter = new StackTracePrinter();

        Netcom netcom = new Netcom(
                new RetryConnecter(
                        new Config(),
                        new RabbitOneTimeConnecter(
                                stackTracePrinter,
                                new ConnectFailedFactory(new MessageFormatter())
                        ),
                        new ConnectFailedFactory(new MessageFormatter())
                ),
                new RabbitRpcCaller()
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
                        new GameLoop()
                ),
                netcom
        );

        processRunner.run();
    }

}
