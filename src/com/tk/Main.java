package com.tk;

public class Main {

    public static void main(String[] args) {
        HttpServer httpServer = new HttpServer(3100);
        httpServer.start();
    }
}
