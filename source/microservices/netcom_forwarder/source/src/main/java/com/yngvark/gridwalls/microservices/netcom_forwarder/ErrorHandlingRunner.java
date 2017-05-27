package com.yngvark.gridwalls.microservices.netcom_forwarder;

import com.yngvark.gridwalls.microservices.netcom_forwarder.app.App;

class ErrorHandlingRunner {
    public void run(App app) {
        try {
            app.run();
        } catch (Throwable throwable) {
            System.out.println("Error occured. Exiting");
            throwable.printStackTrace();
        }
    }
}
