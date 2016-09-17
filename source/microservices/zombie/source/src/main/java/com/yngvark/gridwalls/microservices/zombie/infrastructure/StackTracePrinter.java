package com.yngvark.gridwalls.microservices.zombie.infrastructure;

import org.apache.commons.lang3.exception.ExceptionUtils;

public class StackTracePrinter {
    public void print(String message, Throwable e) {
        System.out.println(message + " - Details: " + ExceptionUtils.getStackTrace(e));
    }
}
