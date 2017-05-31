package zombie;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test_helper.RabbitBroker;
import test_helper.TestHelper;

import java.util.concurrent.TimeUnit;

public class ShutdownMsgTest {
    private TestHelper testHelper;

    @BeforeEach
    public  void beforeEach() {
        testHelper = new TestHelper(new RabbitBroker());
    }

    @AfterEach
    public void afterEach() throws Exception {
        testHelper.stopTestIfNotStopped();
    }

    @Test
    public void should_shutdown_when_receiving_kill_message_from_server() throws Exception {
        // Given
        testHelper.startTest(10);
        testHelper.startProcess();
        testHelper.waitForProcessOutputOrTimeout("Sending message", 3, TimeUnit.SECONDS);
        for (int i = 0; i < 3; i++) {
            testHelper.getEventOrTimeoutAfter(500, TimeUnit.MILLISECONDS);
        }

        // When
        testHelper.publishServerMessage("shutdown");

        // Then
        testHelper.waitForProcessExitOr(3, TimeUnit.SECONDS);
    }
}
