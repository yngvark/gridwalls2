package zombie;

import com.yngvark.gridwalls.netcom.GameRpcServer;
import com.yngvark.gridwalls.netcom.RpcRequestHandler;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

public interface Broker {
    void connect() throws IOException, TimeoutException;

    BlockingQueue<String> consumeEventsFromClient() throws IOException;

    GameRpcServer createRpcServer(String queueName, RpcRequestHandler requestHandler);

    void close() throws IOException;
}
