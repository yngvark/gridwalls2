package com.yngvark.gridwalls.netcom;

import com.yngvark.gridwalls.microservices.zombie.Config;
import com.yngvark.gridwalls.netcom.rabbitmq.BrokerConnecter;

public class NetcomBuilder {
    private String brokerHostname;
    private BrokerConnecter brokerConnecter;
    private RpcCaller rpcCaller;

    public Netcom create() {
        Netcom netcom = new Netcom(
                new RetryConnecter(
                        Config.builder().brokerHostname(brokerHostname).build(),
                        brokerConnecter
                ),
                rpcCaller
        );

        return netcom;
    }

    public NetcomBuilder setBrokerHostname(String brokerHostname) {
        this.brokerHostname = brokerHostname;
        return this;
    }

    public NetcomBuilder setBrokerConnecter(BrokerConnecter brokerConnecter) {
        this.brokerConnecter = brokerConnecter;
        return this;
    }

    public NetcomBuilder setRpcCaller(RpcCaller rpcCaller) {
        this.rpcCaller = rpcCaller;
        return this;
    }
}
