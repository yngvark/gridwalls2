package com.yngvark.gridwalls.microservices.netcom_forwarder;

import com.yngvark.gridwalls.microservices.netcom_forwarder.app.App;

import static org.junit.jupiter.api.Assertions.*;

public class ErrorHandlingTestRunner {
    private final ErrorHandlingRunner errorHandlingRunner;

    public static ErrorHandlingTestRunner create() {
        return new ErrorHandlingTestRunner(new ErrorHandlingRunner());
    }

    public ErrorHandlingTestRunner(ErrorHandlingRunner errorHandlingRunner) {
        this.errorHandlingRunner = errorHandlingRunner;
    }

    public void run(App app) {
        errorHandlingRunner.run(app);
    }
}