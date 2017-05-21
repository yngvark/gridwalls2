package test_helper;

import com.yngvark.gridwalls.netcom.GameRpcServer;
import util.lib.InputStreamListener;
import util.lib.ProcessKiller;
import util.lib.ProcessStarter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TestHelper {
    private final Broker broker;

    private GameRpcServer rpcServer;
    private ExecutorService executorService;
    private BlockingQueue<String> eventsFromClient;
    private Process process;

    private InputStreamListener stdoutListener;
    private InputStreamListener stderrListener;
    private Future stdOutListenFuture;

    private boolean testStarted = false;
    private boolean testStopped = false;

    public TestHelper(Broker broker) {
        this.broker = broker;
    }

    public void startTest(int sleepTimeMillisBetweenTurns) throws IOException, TimeoutException {
        testStarted = true;

        broker.connect();
        rpcServer = broker.createRpcServer("rpc_queue", (String request) -> "[GameInfo] mapHeight=10 mapWidth=10 sleepTimeMillisBetweenTurns=" + sleepTimeMillisBetweenTurns);

        executorService = Executors.newCachedThreadPool();
        executorService.submit(() -> rpcServer.run());

        eventsFromClient = broker.consumeEventsFromClient();
    }

    public void startProcess() throws IOException {
        process = ProcessStarter.startProcess(Config.PATH_TO_APP);

        stdoutListener = new InputStreamListener();
        stdOutListenFuture = stdoutListener.listenInNewThreadOn(process.getInputStream());

        stderrListener = new InputStreamListener();
        stderrListener.listenInNewThreadOn(process.getErrorStream());
    }

    public void waitForProcessOutputOrTimeout(String logText, long time, TimeUnit timeUnit) throws InterruptedException {
        stdoutListener.waitFor(logText, time, timeUnit);
    }

    public String getEventOrTimeoutAfter(int timeout, TimeUnit timeUnit) throws InterruptedException {
        System.out.println("Waiting for event...");

        String event = eventsFromClient.poll(timeout, timeUnit);
        if (event == null)
            throw new RuntimeException("Timeout while waiting for event from client.");

        System.out.println("<- " + event);
        return event;
    }

    public void stopTestIfNotStopped() throws IOException, IllegalAccessException, InterruptedException, NoSuchFieldException, ExecutionException {
        if (!testStarted || testStopped)
            return;
        testStopped = true;

        System.out.println("Stopping test.");
        rpcServer.stop();
        executorService.shutdown();
        broker.close();
        ProcessKiller.killUnixProcess(process);
        ProcessKiller.waitForExitAndAssertExited(process, 3, TimeUnit.SECONDS);
        stdOutListenFuture.get(); // Catches exceptions from the Future.
    }

    public List<String> getProcessOutput() {
        return stdoutListener.getProcessOutput();
    }

    public void waitForProcessExitOr(int timeout, TimeUnit timeUnit) throws InterruptedException, IOException, ExecutionException {
        if (!testStarted || testStopped)
            throw new RuntimeException("testStarted=" + testStarted + " - testStopped=" + testStopped);

        ProcessKiller.waitForExitAndAssertExited(process, timeout, timeUnit);

        rpcServer.stop();
        executorService.shutdown();
        broker.close();
        stdOutListenFuture.get();
    }

    public void publishServerMessage(String msg) throws IOException {
        System.out.println("Publish server message: " + msg);
        broker.publishServerMessage(msg);
    }
}
