package com.zooplus.cats;

import com.zooplus.cats.controller.Controller;
import spark.Service;

public class Main {

    private static final Service http = Service.ignite();

    public static void main(String[] args) {
        registerControllers();
        System.out.println("Server ignited!");
    }

    private static void registerControllers() {
        new Controller(Main.http);
    }

    public static void shutdownServer() {
        System.err.println("Shutting down the server");
        System.exit(1);
    }
}