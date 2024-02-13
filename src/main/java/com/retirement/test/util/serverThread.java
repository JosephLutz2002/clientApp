package com.retirement.test.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.BiConsumer;


public class serverThread implements Runnable {

    private boolean isRunning = true;
    private final int port;
    private final BiConsumer<String,String> logAppender;

    public serverThread(int port, BiConsumer<String,String> logAppender) {
        this.port = port;
        this.logAppender = logAppender;
    }

    public void setIsRunning(boolean status) {
        this.isRunning = status;
    }

    @Override
    public void run() {
        try {
            ServerSocket server = new ServerSocket(port);
            //logAppender.accept("Starting server on port " + port);
            while (isRunning) {
                Socket socket = server.accept();
                new Thread(new ClientHandler(socket, logAppender)).start();
            }

            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final BiConsumer<String,String> logAppender;

        public ClientHandler(Socket clientSocket, BiConsumer<String,String> logAppender) {
            this.clientSocket = clientSocket;
            this.logAppender = logAppender;
        }

        @Override
        public void run() {
            try {
                InputStream inputStream = clientSocket.getInputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    String receivedData = new String(buffer, 0, bytesRead);
                    System.out.println(receivedData.split(",")[1]);
                    logAppender.accept(receivedData.split(",")[0],receivedData.split(",")[1]);
                }
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
