package com.yngvark.communicate_through_named_pipes.input;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;
import static org.slf4j.LoggerFactory.getLogger;

class InputFileLineReaderTest {
    @Test
    public void reading_from_a_closed_stream_should_throw_exception() throws IOException {
        // Given
        BufferedReader bufferedReader = new BufferedReader(new StringReader("Hello"));
        InputFileLineReader reader = new InputFileLineReader(bufferedReader);
        reader.closeStream();

        // When+Then
        assertThrows(IORuntimeException.class, reader::readLine);
    }


}