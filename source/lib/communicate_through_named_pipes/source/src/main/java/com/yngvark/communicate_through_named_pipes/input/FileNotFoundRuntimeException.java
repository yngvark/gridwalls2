package com.yngvark.communicate_through_named_pipes.input;

import java.io.IOException;

/**
 * Unchecked wrapper class for {@link java.io.FileNotFoundException}.
 */
public class FileNotFoundRuntimeException extends RuntimeException {
    private final IOException e;

    public FileNotFoundRuntimeException(IOException e) {
        this.e = e;
    }
}
