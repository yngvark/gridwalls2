package com.yngvark.gridwalls.microservices.zombie;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Main {
    public static void main(String[] argv) throws IOException, TimeoutException, InterruptedException {
        runGame();
        //test();
    }

    private static void test() throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Type 'xx' to quit.");

        while (true) {

            String command = scanner.next();
            System.out.println("You wrote: " + command);
            if (command.equals("xx"))
                break;

            Thread.sleep(1000);
        }
    }

    private static boolean continueRunning = true;

    private static void runGame() throws IOException, TimeoutException, InterruptedException {
        Object exitLock = new Object();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                super.run();
                System.out.println("EXITING GRACEULLY 1");
                continueRunning = false;

                synchronized (exitLock) {
                    try {
                        exitLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        // Connect to server and get following info
        int mapHeight = 10;
        int mapWidth = 10;

        // Connection
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("rabbithost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare("ZombieMoved", "fanout", true);

        // Game initialization
        MapCoordinates mapCoordinates = new MapCoordinates(mapHeight, mapWidth);
        CoordinateFactory coordinateFactory = new CoordinateFactory(mapCoordinates);
        Coordinate initCoord = mapCoordinates.center();
        Publisher publisher = new Publisher(new ZombieMovedSerializer(new CoordinateSerializer()), channel);

        Zombie zombie1 = new Zombie(coordinateFactory, UUID.randomUUID(), initCoord);
        Zombie zombie2 = new Zombie(coordinateFactory, UUID.randomUUID(), initCoord);

        // Game loop, For now, start this whenever we feel like. Later: Start at exact time slot in duration of a turn.
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        Runnable zombieTask1 = () -> {
            try {
                System.out.println("zombie1.nextTurn()");
                ZombieMoved event = zombie1.nextTurn();
                publisher.publishEvent(event);

            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        Runnable zombieTask2 = () -> {
            try {
                System.out.println("zombie2.nextTurn()");
                ZombieMoved event = zombie2.nextTurn();
                publisher.publishEvent(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        executor.scheduleWithFixedDelay(zombieTask1, 0,     2000, TimeUnit.MILLISECONDS);
        executor.scheduleWithFixedDelay(zombieTask2, 1000,  2000, TimeUnit.MILLISECONDS);

        // How to exit game.
        while (continueRunning) {
            Thread.sleep(1000);
        }

        System.out.println("Waiting for Zombies to complete...");
        executor.shutdown();
        executor.awaitTermination(3000, TimeUnit.MILLISECONDS);
        System.out.println("Waiting for Zombies to complete... done.");

        channel.close();
        connection.close();

        System.out.println("Notifying lock.");

        synchronized (exitLock) {
            exitLock.notify();
        }

        System.out.println("Exiting.");
    }

}
