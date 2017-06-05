package com.yngvark.communicate_through_named_pipes.input;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.slf4j.LoggerFactory.getLogger;

class InputFileReaderTest {
    private final Logger logger = getLogger(getClass());

    @Test
    public void closing_a_reader_while_it_is_consuming_should_not_throw_exception() throws IOException {
        // Given
        BufferedReader bufferedReader = new BufferedReader(new StringReader("Hello"));
        InputFileReader reader = new InputFileReader(bufferedReader);

        // When
        reader.consume((msg) -> {
            logger.info("Consumed: {}", msg);
            try {
                Thread.sleep(200l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            reader.closeStream();
        });

        // Then
        // Verify that stream is closed by attempting to read from it
        assertThrows(IOException.class, () -> bufferedReader.read());
    }


}