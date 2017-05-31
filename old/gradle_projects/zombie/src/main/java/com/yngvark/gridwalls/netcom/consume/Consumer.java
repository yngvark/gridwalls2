package com.yngvark.gridwalls.netcom.consume;

import com.yngvark.gridwalls.netcom.connection.ConnectionWrapper;
import com.yngvark.gridwalls.netcom.publish.NetcomResult;

public interface Consumer<T extends ConnectionWrapper> {
    NetcomResult startConsume(T connectionWrapper, String queueName, ConsumeHandler handler);
}
