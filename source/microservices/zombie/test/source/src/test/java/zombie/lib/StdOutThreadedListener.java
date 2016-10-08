package zombie.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class StdOutThreadedListener {
    private ExecutorService executorService;
    private boolean continueReading = true;

    private boolean waitForLogText = false;
    private String logTextToWaitFor = "";
    private Object logTextToWaitForLock = new Object();

    public void listen(InputStream inputStream) {
        System.out.println("Listening on inputstream.");
        BufferedReader appOutStream = new BufferedReader(new InputStreamReader(inputStream));
        executorService = Executors.newCachedThreadPool();

        executorService.submit(() -> {
            readFrom(appOutStream);
        });
    }

    private void readFrom(BufferedReader appOutStream) {
        while (continueReading) {
            String line = null;
            try {
                line = appOutStream.readLine();

                synchronized (logTextToWaitForLock) {
                    if (waitForLogText && line.equals(logTextToWaitFor)) {
                        waitForLogText = false;
                        logTextToWaitForLock.notify();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (line == null)
                return;

            log(line);
        }
    }

    private void log(String line) {
        System.out.println("[PROCESS] \"" + line + "\"");
    }

    public void waitFor(String logText, long timeoutInMilliseconds) {
        System.out.println("Waiting for process output: " + logText);

        this.logTextToWaitFor = logText;
        waitForLogText = true;

        long timeoutNanos = toNanos(timeoutInMilliseconds);
        long endTime = System.nanoTime() + timeoutNanos;

        synchronized (logTextToWaitForLock) {
            try {
                while (waitForLogText) {
                    long sleepTime = endTime - System.nanoTime();
                    if (sleepTime < 0)
                        break;
                    logTextToWaitForLock.wait(sleepTime); // Avoid spurious wake ups.
                }
                waitForLogText = false;

            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println("Stopped waiting for process output.");
            }
        }
    }

    private long toNanos(long timeInMilliseconds) {
        return (long) (timeInMilliseconds * Math.pow(10, 6));
    }

    private long toMillis(long nanotime) {
        return (long) (nanotime / Math.pow(10, 6));
    }

    public void stopListening() {
        continueReading = false;
        executorService.shutdown();
        System.out.println("Listening on inputstream has shut down.");
    }
}
