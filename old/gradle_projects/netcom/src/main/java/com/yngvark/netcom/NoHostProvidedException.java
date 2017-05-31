package com.yngvark.netcom;

public class NoHostProvidedException extends RuntimeException {
    public NoHostProvidedException() {
    }

    public NoHostProvidedException(String s) {
        super(s);
    }

    public NoHostProvidedException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public NoHostProvidedException(Throwable throwable) {
        super(throwable);
    }

    public NoHostProvidedException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
