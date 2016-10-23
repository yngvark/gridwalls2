package com.yngvark.gridwalls.netcom;

import com.yngvark.gridwalls.microservices.zombie.Config;
import com.yngvark.gridwalls.netcom.rabbitmq.BrokerConnecter;

public class NetcomBuilder<T extends ConnectionWrapper> {
    private String brokerHostname;
    private BrokerConnecter<T> brokerConnecter;
    private RpcCaller<T> rpcCaller;

    public Netcom<T> create() {
        Netcom<T> netcom = new Netcom<>(
                new RetryConnecter<>(
                        Config.builder().brokerHostname(brokerHostname).build(),
                        brokerConnecter
                ),
                rpcCaller
        );

        return netcom;
    }

    public NetcomBuilder<T> setBrokerHostname(String brokerHostname) {
        this.brokerHostname = brokerHostname;
        return this;
    }

    public NetcomBuilder<T> setBrokerConnecter(BrokerConnecter<T> brokerConnecter) {
        this.brokerConnecter = brokerConnecter;
        return this;
    }

    public NetcomBuilder<T> setRpcCaller(RpcCaller<T> rpcCaller) {
        this.rpcCaller = rpcCaller;
        return this;
    }
}
