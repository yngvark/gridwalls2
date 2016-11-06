package zombie;

import org.junit.jupiter.api.Test;
import zombie.lib.ProcessKiller;
import zombie.lib.ProcessStarter;
import zombie.lib.InputStreamListener;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProcessTest {
    @Test
    public void should_be_able_to_kill_process_within_3_seconds() throws IOException, InterruptedException, NoSuchFieldException, IllegalAccessException {
        // Given
        Process process = ProcessStarter.startProcess(Config.PATH_TO_APP);

        InputStreamListener inputStreamListener = new InputStreamListener();
        inputStreamListener.listenInNewThreadOn(process.getInputStream());
        inputStreamListener.waitFor("Receiving game config.", 2, TimeUnit.SECONDS);

        InputStreamListener stderrListener = new InputStreamListener();
        stderrListener.listenInNewThreadOn(process.getErrorStream());

        // When
        ProcessKiller.killUnixProcess(process);

        // Then
        ProcessKiller.waitForExitAndAssertExited(process, 3, TimeUnit.SECONDS);
        inputStreamListener.stopListening();
    }
}

