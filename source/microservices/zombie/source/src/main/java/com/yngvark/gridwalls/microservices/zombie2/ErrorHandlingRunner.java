package com.yngvark.gridwalls.microservices.zombie2;

import com.yngvark.gridwalls.microservices.zombie2.app.App;

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
