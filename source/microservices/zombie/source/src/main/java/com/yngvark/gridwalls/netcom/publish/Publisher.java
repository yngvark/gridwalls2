package com.yngvark.gridwalls.netcom.publish;

import com.yngvark.gridwalls.netcom.connection.ConnectionWrapper;

public interface Publisher<T extends ConnectionWrapper> {
    PublishResult publish(T connectionWrapper, String queue, String message);
}
