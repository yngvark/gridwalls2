package com.yngvark.gridwalls.netcom.consume;

import com.yngvark.gridwalls.netcom.publish.NetcomResult;

public interface Consumer {
    NetcomResult startConsume(ConsumeHandler handler);
}
