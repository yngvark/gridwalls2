package com.yngvark.gridwalls.microservices.zombie.netcom;

public class ConnectException extends RuntimeException {
    public ConnectException() {
    }

    public ConnectException(String s) {
        super(s);
    }

    public ConnectException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ConnectException(Throwable throwable) {
        super(throwable);
    }

    public ConnectException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
