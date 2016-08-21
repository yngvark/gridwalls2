package com.yngvark.gridwalls.microservices.zombie;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.inject.Inject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class SystemInputCommandExecutor {
    private final ExecutorService executor;
    private final BiMap<String, Runnable> commands = HashBiMap.create();

    @Inject
    public SystemInputCommandExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    public void handleCommand(String command, Runnable runnable) {
        commands.put(command, runnable);
    }

    public void readFromStdIn() {
        try {
            tryToReadFromStdIn();
        } catch (IOException e) {
            System.out.println("Error occurred while reading from stdin.");
            e.printStackTrace();
        }
    }

    private void tryToReadFromStdIn() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String input;

        while (true) {
            input = readInputLine(br);
            if (input == null)
                break;

            if ("exit".equals(input)) {
                System.out.println("Exiting.");
                break;
            }

            handleInput(input);
        }

        exit();
    }

    private void handleInput(String input) {
        System.out.println("You entered: " + input);

        executeCommand(input);
        sleep(50);
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            System.out.println(InterruptedException.class.getSimpleName() + " while waiting.");
            e.printStackTrace();
        }
    }

    private String readInputLine(BufferedReader br) throws IOException {
        String input;
        System.out.println("");
        System.out.print("Enter something: ");

        input = br.readLine();

        System.out.print("");
        return input;
    }

    private void executeCommand(String input) {
        Runnable command = commands.get(input);

        if (command != null) {
            System.out.println("Running command: " + input);

            CompletableFuture
                    .runAsync(command, executor)
                    .thenRun(() -> System.out.println("Command '" + input + "' completed."));
        }
    }

    public void exit() {
        System.out.println("Shutting down all threads (waiting max 5 seconds)...");
        executor.shutdown();
        try {
            awaitTermination();
        } catch (InterruptedException e) {
            System.out.println("Interrupted while shutting down all thread.");
            e.printStackTrace();
        }

        System.out.println("Shutdown complete.");
    }

    private void awaitTermination() throws InterruptedException {
        boolean success = executor.awaitTermination(5, TimeUnit.SECONDS);
        if (success) {
            System.out.println("Shutting down all threads (waiting max 5 seconds)... done");
        } else {
            System.out.println("Could not gracefully exit running threads. Forcing exit.");
            printRunnablesThatWereNeverStarted();
        }
    }

    private void printRunnablesThatWereNeverStarted() {
        List<Runnable> runnablesThatWereNeverStarted = executor.shutdownNow();

        if (runnablesThatWereNeverStarted.size() > 0)
            System.out.println("Commands that never had time to start:");
        for (Runnable runnable : runnablesThatWereNeverStarted)
            printCommand(runnable);
    }

    private void printCommand(Runnable runnable) {
        if (commands.containsValue(runnable)) {
            String command = commands.inverse().get(runnable);
            System.out.println(command);
        }
    }
}
