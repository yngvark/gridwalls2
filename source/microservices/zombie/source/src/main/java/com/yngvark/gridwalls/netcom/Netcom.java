package com.yngvark.gridwalls.netcom;

import com.yngvark.gridwalls.netcom.connection.BrokerConnecterHolder;
import com.yngvark.gridwalls.netcom.connection.ConnectionWrapper;
import com.yngvark.gridwalls.netcom.connection.connect_status.ConnectionStatus;
import com.yngvark.gridwalls.netcom.consume.ConsumeHandler;
import com.yngvark.gridwalls.netcom.consume.Consumer;
import com.yngvark.gridwalls.netcom.publish.NetcomFailed;
import com.yngvark.gridwalls.netcom.publish.NetcomResult;
import com.yngvark.gridwalls.netcom.publish.Publisher;
import com.yngvark.gridwalls.netcom.rpc.RpcCaller;
import com.yngvark.gridwalls.netcom.rpc.RpcFailed;
import com.yngvark.gridwalls.netcom.rpc.RpcResult;

public class Netcom<T extends ConnectionWrapper> {
    private final BrokerConnecterHolder<T> brokerConnecterHolder;
    private final RpcCaller<T> rpcCaller;
    private final Publisher<T> publisher;
    private final Consumer consumer;

    public Netcom(BrokerConnecterHolder<T> brokerConnecterHolder, RpcCaller<T> rpcCaller, Publisher<T> publisher,
            Consumer consumer) {
        this.brokerConnecterHolder = brokerConnecterHolder;
        this.rpcCaller = rpcCaller;
        this.publisher = publisher;
        this.consumer = consumer;
    }

    public RpcResult rpcCall(String rpcQueueName, String message) {
        ConnectionStatus<T> connectionStatus = brokerConnecterHolder.connectIfNotConnected();
        if (connectionStatus.connected())
            return rpcCaller.rpcCall(connectionStatus.getConnectionWrapper(), rpcQueueName, message);
        else
            return new RpcFailed("Could not connect. Details: " + connectionStatus.getConnectFailedDetails());
    }

    public NetcomResult publish(String queueName, String message) {
        ConnectionStatus<T> connectionStatus = brokerConnecterHolder.connectIfNotConnected();
        if (connectionStatus.connected()) {
            return publisher.publish(connectionStatus.getConnectionWrapper(), queueName, message);
        } else {
            return new NetcomFailed("Could not publish. Details: " + connectionStatus.getConnectFailedDetails());
        }
    }

    public NetcomResult startConsume(String queueName, ConsumeHandler handler) {
        ConnectionStatus<T> connectionStatus = brokerConnecterHolder.connectIfNotConnected();
        if (connectionStatus.connected()) {
            return consumer.startConsume(connectionStatus.getConnectionWrapper(), queueName, handler);
        } else {
            return new NetcomFailed("Could not consume. Details: " + connectionStatus.getConnectFailedDetails());
        }
    }

    public void disconnectAndDisableReconnect() {
        System.out.println("Netcom disconnectAndDisableReconnect");
        brokerConnecterHolder.disconnectAndDisableReconnect();
    }
}
