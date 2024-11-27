package com.tk;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer extends Thread{

    private ExecutorService service = Executors.newCachedThreadPool();

    private int port;

    public HttpServer(int port){
        this.port = port;
    }

    @Override
    public void run() {
        try (ServerSocket server = new ServerSocket(port)) {
            while (true) {
                this.serverProcess(server);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void serverProcess(ServerSocket server) throws IOException {
        Socket socket = server.accept();

        this.service.execute(() -> {
            try (
                    InputStream in = socket.getInputStream();
                    OutputStream out = socket.getOutputStream();
            ) {

                HttpRequest request = new HttpRequest(in);
                HttpHeader header = request.getHeader();

                if (header.isGetMethod()) {
                    File file = null;

                    if(header.getPath().equalsIgnoreCase("/")){
                        file = new File(".", "/index.html");
                    } else {
                        file = new File(".", header.getPath());
                    }

                    if (file.exists() && file.isFile()) {
                        this.respondLocalFile(file, out);
                    } else {
                        this.respondNotFoundError(out);
                    }
                } else {
                    this.respondOk(out);
                }
            } catch (EmptyRequestException e) {
                e.printStackTrace();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void respondNotFoundError(OutputStream out) throws IOException {
        HttpResponse response = new HttpResponse(Status.NOT_FOUND);
        response.addHeader("Content-Type", ContentType.TEXT_PLAIN);
        response.setBody(Status.NOT_FOUND.getText());
        response.writeTo(out);
    }

    private void respondLocalFile(File file, OutputStream out) throws IOException {
        HttpResponse response = new HttpResponse(Status.OK);
        response.setBody(file);
        response.writeTo(out);
    }

    private void respondOk(OutputStream out) throws IOException {
        HttpResponse response = new HttpResponse(Status.OK);
        response.writeTo(out);
    }
}
