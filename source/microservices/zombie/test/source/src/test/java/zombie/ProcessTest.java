package zombie;

import org.junit.jupiter.api.Test;
import to_be_deleted.CommandExecutor;
import zombie.lib.ProcessKiller;
import zombie.lib.ProcessStarter;
import zombie.lib.StdOutThreadedListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProcessTest {
    @Test
    public void should_be_able_to_kill_process_within_3_seconds() throws IOException, InterruptedException, NoSuchFieldException, IllegalAccessException {
        // Given
        StdOutThreadedListener stdOutThreadedListener = new StdOutThreadedListener();
        Process process = ProcessStarter.startProcess(Config.PATH_TO_APP);
        stdOutThreadedListener.listen(process.getInputStream());
        stdOutThreadedListener.waitFor("Receiving game config.", 2000);

        // When
        ProcessKiller.killUnixProcess(process);

        // Then
        ProcessKiller.waitForExitAndAssertExited(process, 3, TimeUnit.SECONDS);
        stdOutThreadedListener.stopListening();
    }
}

