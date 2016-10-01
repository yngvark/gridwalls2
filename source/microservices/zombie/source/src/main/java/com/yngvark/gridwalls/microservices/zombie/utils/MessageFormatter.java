package com.yngvark.gridwalls.microservices.zombie.utils;

import java.text.MessageFormat;

public class MessageFormatter {
    public String format(String text, Object... arguments) {
        try {
            return MessageFormat.format(text, arguments);
        } catch (IllegalArgumentException e) {
            return "Could not format message: " + text;
        }
    }
}
