package test_helper;

import com.yngvark.gridwalls.netcom.GameRpcServer;
import util.lib.InputStreamListener;
import util.lib.ProcessKiller;
import util.lib.ProcessStarter;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TestHelper {
    private final Broker broker;

    private GameRpcServer rpcServer;
    private ExecutorService executorService;
    private BlockingQueue<String> eventsFromClient;
    private Process process;

    public TestHelper(Broker broker) {
        this.broker = broker;
    }

    public void startTest() throws IOException, TimeoutException {
        broker.connect();
        rpcServer = broker.createRpcServer("rpc_queue", (String request) -> "[GameInfo] mapHeight=10 mapWidth=10");

        executorService = Executors.newCachedThreadPool();
        executorService.submit(() -> rpcServer.run());

        eventsFromClient = broker.consumeEventsFromClient();
    }

    public void startProcess() throws IOException {
        process = ProcessStarter.startProcess(Config.PATH_TO_APP);
    }

    public void waitForProcessOutput(String logText, long time, TimeUnit timeUnit) throws InterruptedException {
        InputStreamListener stdoutListener = new InputStreamListener();
        stdoutListener.listenInNewThreadOn(process.getInputStream());

        InputStreamListener stderrListener = new InputStreamListener();
        stderrListener.listenInNewThreadOn(process.getErrorStream());
    }

    public String getEvent(int timeout, TimeUnit timeUnit) throws InterruptedException {
        System.out.println("Waiting for event...");

        String event = eventsFromClient.poll(timeout, timeUnit);
        if (event == null)
            throw new RuntimeException("Event was null");

        System.out.println("-> " + event);
        return event;
    }

    public void stopTest() throws IOException, IllegalAccessException, InterruptedException, NoSuchFieldException {
        System.out.println("Stopping test.");
        rpcServer.stop();
        executorService.shutdown();
        broker.close();
        ProcessKiller.killUnixProcess(process);
        ProcessKiller.waitForExitAndAssertExited(process, 3, TimeUnit.SECONDS);
    }
}
