package com.yngvark.gridwalls.microservices.zombie.netcom;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

public class RetryConnecterOldTest {
    @Test
    public void should_print_list_of_connection_errors_when_retry_connection() throws IOException {
        Connection connectionMock = Mockito.mock(Connection.class);
        NetcomOld netcomOld = new NetcomOld();

        Mockito.when(connectionMock.createChannel()).thenReturn(Mockito.mock(Channel.class));


        String response = netcomOld.rpcCall(RPC_QUEUE_NAME, "hello");
    }

}