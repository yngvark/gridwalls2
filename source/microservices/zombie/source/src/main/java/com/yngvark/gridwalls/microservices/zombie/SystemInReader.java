package com.yngvark.gridwalls.microservices.zombie;

import com.google.inject.Inject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SystemInReader { // TODO: test.
    private final StackTracePrinter stackTracePrinter;
    private final CommandHandler commandHandler;

    private BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

    @Inject
    public SystemInReader(StackTracePrinter stackTracePrinter, CommandHandler commandHandler) {
        this.stackTracePrinter = stackTracePrinter;
        this.commandHandler = commandHandler;
    }

    public void run() {
        try {
            tryToReadFromStdIn();
        } catch (IOException e) {
            stackTracePrinter.print("Error occurred while reading from stdin.", e);
        }
    }

    private void tryToReadFromStdIn() throws IOException {
        while (true) {
            String input = readNonNullLineFromSystemInput();

            if (input.equals("exit"))
                break;
            if (Thread.currentThread().isInterrupted()) {
                System.out.println("Command ignored. Thread is interrupted.");
                break;
            }

            handleInput(input);
        }

        System.out.println("Exiting input reader.");
    }

    private String readNonNullLineFromSystemInput() throws IOException {
        System.out.println("");
        System.out.print("Enter something: ");
        String input = read();
        System.out.print("");
        return input;
    }

    private String read() throws IOException {
        String input = bufferedReader.readLine();
        if (input == null) {
            bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            return read();
        }

        return input;
    }

    private void handleInput(String input) {
        System.out.println("You entered: " + input);

        commandHandler.handle(input);
        sleep(10);
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            stackTracePrinter.print("Interrupted while sleeping.", e);
        }
    }
}