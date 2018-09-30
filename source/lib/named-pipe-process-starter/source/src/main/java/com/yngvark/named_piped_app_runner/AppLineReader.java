package com.yngvark.named_piped_app_runner;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

public class AppLineReader {
    public static String readLine(NamedPipeProcess app) {
        return assertTimeoutPreemptively(Duration.ofMillis(2000), app.inputFileLineReader::readLine);
    }
}
