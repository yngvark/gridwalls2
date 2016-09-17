package com.yngvark.gridwalls.microservices.zombie.netcom;

public class CouldNotConnectException extends RuntimeException {
    public CouldNotConnectException() {
    }

    public CouldNotConnectException(String s) {
        super(s);
    }

    public CouldNotConnectException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public CouldNotConnectException(Throwable throwable) {
        super(throwable);
    }

    public CouldNotConnectException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
