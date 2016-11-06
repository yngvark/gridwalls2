package com.yngvark.gridwalls.microservices.zombie.game.utils;

import java.text.MessageFormat;
import java.util.Arrays;

public class SafeMessageFormatter {
    public static String format(String text, Object... args) {
        try {
            return MessageFormat.format(text, args); // Can throw IllegalArgumentException.
        } catch (Throwable e) {
            String argsJoined = Arrays.toString(args);
            System.err.println("Could not format text: " + text + " with args " + argsJoined);
            return text + " [Could not format text. Args: " + argsJoined + ". Details: " + e.getMessage() + "]";
        }
    }
}

