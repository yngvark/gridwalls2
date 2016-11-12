package zombie;

import org.junit.jupiter.api.Test;
import test_helper.RabbitBroker;
import test_helper.TestHelper;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class QueueBasedTest {
    @Test
    public void should_produe_zombie_moves_after_receiving_gameconfig() throws Exception {
        // Given
        TestHelper testHelper = new TestHelper(new RabbitBroker());
        testHelper.startTest();

        // When
        testHelper.startProcess();

        // Then
        testHelper.waitForProcessOutput("Receiving game config.", 5, TimeUnit.SECONDS);
        for (int i = 0; i < 3; i++) {
            String event = testHelper.getEvent(100, TimeUnit.MILLISECONDS);
            assertTrue(event.startsWith("[ZombieMoved]"));
        }

        // Finally
        testHelper.stopTest();
    }
}
