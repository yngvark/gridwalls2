package com.yngvark.communicate_through_named_pipes.input;

import java.io.IOException;

/**
 * Unchecked wrapper class for {@link IOException}.
 */
public class IORuntimeException extends RuntimeException {
    public IORuntimeException(IOException e) {
        super(e);
    }
}
