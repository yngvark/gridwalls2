package com.yngvark.gridwalls.microservices.zombie.netcom;

public class RpcException extends RuntimeException {
    public RpcException() {
    }

    public RpcException(String s) {
        super(s);
    }

    public RpcException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public RpcException(Throwable throwable) {
        super(throwable);
    }

    public RpcException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
