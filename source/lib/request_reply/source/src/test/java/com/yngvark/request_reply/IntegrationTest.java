package com.yngvark.request_reply;

import com.yngvark.named_piped_app_runner.NamedPipeProcess;
import com.yngvark.named_piped_app_runner.NamedPipeProcessRunner;
import com.yngvark.named_piped_app_runner.NamedPipeProcessRunnerFactory;
import com.yngvark.named_piped_app_runner.NamedPipeProcessStarter;
import com.yngvark.process_test_helper.TestableApp;
import com.yngvark.process_test_helper.TestableAppFactory;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.slf4j.LoggerFactory.getLogger;

class IntegrationTest {
    private final Logger logger = getLogger(getClass());

    @Test
    void should_subscribe_to_given_request_queue() throws Exception {
        // Given


        NamedPipeProcess process = NamedPipeProcessStarter.start();

        // When
        List<String> receivedMessages = consumeExpectedMessages(process, 1);

        // When
        assertEquals("/subscribeTo ")
    }

    private List<String> consumeExpectedMessages(TestableApp app, int expectedMessageCount) {
        List<String> receivedMessages = new ArrayList<>();
        Counter counter = new Counter();

        try {
            app.inputFileReader.consume((msg) -> {
                logger.info("<<< Msg: " + msg);
                receivedMessages.add(msg);
                counter.increase();
                if (counter.value() == expectedMessageCount)
                    app.inputFileReader.closeStream();
            });
            return receivedMessages;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createFileIfNotExists(Path buildDir, String file) throws IOException {
        if (!Files.exists(buildDir)) {
            Files.createDirectories(buildDir);
        }

        if (Files.exists(Paths.get(file))) {
            Files.delete(Paths.get(file));
        }
    }

}