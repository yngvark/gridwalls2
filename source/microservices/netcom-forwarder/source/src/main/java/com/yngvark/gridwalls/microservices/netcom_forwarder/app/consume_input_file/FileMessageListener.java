package com.yngvark.gridwalls.microservices.netcom_forwarder.app.consume_input_file;

public interface FileMessageListener {
    void messageReceived(String msg);
}
