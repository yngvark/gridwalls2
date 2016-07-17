package com.yngvark.gridwalls.microservices.zombie;

import com.yngvark.gridwalls.netcom.NetCom;

public class TestNetComFactory {
    public static TestNetCom create() {
        return new TestNetCom();
    }
}
