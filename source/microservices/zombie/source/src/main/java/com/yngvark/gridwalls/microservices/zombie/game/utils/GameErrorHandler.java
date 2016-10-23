package com.yngvark.gridwalls.microservices.zombie.game.utils;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameErrorHandler {
    private List<Throwable> throwables = new CopyOnWriteArrayList<>();

    public void handle(Throwable e) {
        throwables.add(e);
    }

    public String getErrors() {
        if (throwables.size() == 0)
            return "";

        StringBuilder stringBuilder = new StringBuilder();
        for (Throwable throwable : throwables) {
            stringBuilder.append(ExceptionUtils.getStackTrace(throwable));
        }

        return stringBuilder.toString();

    }

    public boolean receivedErrors() {
        return throwables.size() > 0;
    }
}
