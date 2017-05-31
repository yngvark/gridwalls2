package zombie;

import org.junit.Ignore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test_helper.RabbitBroker;
import test_helper.TestHelper;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TurnDuriationTest {
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
    public void time_spent_for_each_turn_should_be_below_100_ms() throws Exception {
        // Given
        testHelper.startTest(10);

        // When
        testHelper.startProcess();
        testHelper.waitForProcessOutputOrTimeout("Sending message", 5, TimeUnit.SECONDS);
        for (int i = 0; i < 20; i++) {
            testHelper.getEventOrTimeoutAfter(100, TimeUnit.MILLISECONDS);
        }
        testHelper.stopTestIfNotStopped();

        // Then
        List<String> processOutput = testHelper.getProcessOutput();

        LocalTime sendTime = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("kk:mm:ss.SSS");
        for (String line : processOutput) {
            if (!line.contains("Sending message"))
                continue;

            System.out.println(line);

            String time = line.substring(line.indexOf("[") + 1, line.indexOf("]"));
            LocalTime lastTime = sendTime;
            sendTime = LocalTime.parse(time, formatter);

            if (lastTime != null) {
                Duration duration = Duration.between(lastTime, sendTime);
                System.out.println("Duration: " + duration.toMillis() + " - from " + lastTime.format(formatter) + " -to- " + sendTime.format(formatter));
                assertTrue(duration.toMillis() < 100);
            }
        }
    }

    @Test
    @Ignore
    public void testParse() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("kk:mm:ss.SSS");
        System.out.println(LocalTime.now().format(formatter));

        LocalTime lastTime = LocalTime.parse("08:39:23.000", formatter);
        LocalTime sendTime = LocalTime.parse("08:39:23.001", formatter);

        Duration duration = Duration.between(lastTime, sendTime);
        System.out.println("Duration: " + duration.toMillis() + " - from " + lastTime.format(formatter) + " -to- " + sendTime.format(formatter));
        System.out.println("Duration: " + duration.toNanos() + " - from " + lastTime.format(formatter) + " -to- " + sendTime.format(formatter));
    }
}
