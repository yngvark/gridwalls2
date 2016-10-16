package zombie.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class InputStreamListener {
    private boolean listening;

    private ExecutorService executorService;
    private boolean continueReading = true;

    private boolean waitForLogText = false;
    private String logTextToWaitFor = "";
    private BlockingQueue blockingQueue = new ArrayBlockingQueue(1);

    public synchronized void listenInNewThreadOn(InputStream inputStream) {
        if (listening)
            throw new RuntimeException("Can onle be called once.");
        listening = true;

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
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (line == null)
                return;

            if (waitForLogText && line.equals(logTextToWaitFor)) {
                waitForLogText = false;
                try {
                    blockingQueue.put(new Object());
                } catch (InterruptedException e) {
                    throw new RuntimeException("This should not happen. You can only call this method once.");
                }
            }

            log(line);
        }
    }

    public void waitFor(String logText, long time, TimeUnit timeUnit) throws InterruptedException {
        System.out.println("Waiting for text.");
        logTextToWaitFor = logText;
        waitForLogText = true;

        Object result = blockingQueue.poll(time, timeUnit);
        if (result == null)
            throw new RuntimeException("Did not receive text in time: " + logText);

        System.out.println("Found text! Continuing.");
    }

    private void log(String line) {
        System.out.println("[PROCESS] \"" + line + "\"");
    }

    public void stopListening() {
        continueReading = false;
        executorService.shutdown();
        System.out.println("Listening on inputstream has shut down.");
    }
}
