package com.yngvark.gridwalls.microservices.zombie.game.utils;

import java.text.MessageFormat;

public class SafeMessageFormatter {
    public String format(String text, Object... arguments) {
        try {
            return MessageFormat.format(text, arguments);
        } catch (IllegalArgumentException e) {
            return "Could not format message: " + text;
        }
    }
}
