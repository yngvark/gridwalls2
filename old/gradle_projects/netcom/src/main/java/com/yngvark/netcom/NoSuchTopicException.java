package com.yngvark.netcom;

public class NoSuchTopicException extends RuntimeException {
    public NoSuchTopicException() {
    }

    public NoSuchTopicException(String s) {
        super(s);
    }

    public NoSuchTopicException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public NoSuchTopicException(Throwable throwable) {
        super(throwable);
    }

    public NoSuchTopicException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
