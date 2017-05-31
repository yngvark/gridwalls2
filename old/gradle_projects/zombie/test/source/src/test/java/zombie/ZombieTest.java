package zombie;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test_helper.RabbitBroker;
import test_helper.TestHelper;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ZombieTest {
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
    public void should_produe_zombie_moves_after_receiving_gameconfig() throws Exception {
        // Given
        testHelper.startTest(10);

        // When
        testHelper.startProcess();

        // Then
        testHelper.waitForProcessOutputOrTimeout("Receiving game config.", 3, TimeUnit.SECONDS);

        for (int i = 0; i < 3; i++) {
            String event = testHelper.getEventOrTimeoutAfter(500, TimeUnit.MILLISECONDS);
            assertTrue(event.startsWith("[ZombieMoved]"));
        }
    }
}
