package com.yngvark.netcom;

public class NotYetConnectedException extends RuntimeException {
    public NotYetConnectedException() {
    }

    public NotYetConnectedException(String s) {
        super(s);
    }

    public NotYetConnectedException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public NotYetConnectedException(Throwable throwable) {
        super(throwable);
    }

    public NotYetConnectedException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
