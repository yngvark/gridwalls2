package com.yngvark.communicate_through_named_pipes.input;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class InputFileLineReaderTest {
    @Test
    public void should_read_one_line() throws IOException {
        // Given
        String testFilename = getClass().getResource("/test.txt").getPath();
        InputFileOpener inputFileOpener = new InputFileOpener(testFilename);

        InputFileLineReader lineReader = inputFileOpener.openLineStream(() -> {
            throw new RuntimeException("Should not retry");
        });

        // Then
        assertEquals("Hei", lineReader.readLine());
        assertEquals("på", lineReader.readLine());
        assertEquals("deg", lineReader.readLine());

        // Finally
        lineReader.closeStream();
    }

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