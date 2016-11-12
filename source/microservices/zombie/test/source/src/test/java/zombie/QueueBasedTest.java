package zombie;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class QueueBasedTest {
    @Test
    public void should_produe_zombie_moves_after_receiving_gameconfig() throws Exception {
        // Given
        TestHelper testHelper = new TestHelper();
        testHelper.startTest();

        // When
        testHelper.startProcess();

        // Then
        testHelper.waitForProcessOutput("Receiving game config.", 5, TimeUnit.SECONDS);
        for (int i = 0; i < 3; i++) {
            String event = testHelper.getEvent(1200, TimeUnit.MILLISECONDS);
            assertTrue(event.startsWith("[ZombieMoved]"));
        }

        // Finally
        testHelper.stopTest();
    }

}
