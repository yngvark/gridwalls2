package com.yngvark.gridwalls.micrfoservices.zombie_test;

import com.yngvark.named_piped_app_runner.NamedPipeProcess;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class AppLineReader {
    public static String readLine(NamedPipeProcess app) {
        return assertTimeoutPreemptively(Duration.ofMillis(200), app.inputFileLineReader::readLine);
    }
}
