package org.casper.learning.io.bio;

import lombok.Data;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

@Data
public class BootServer {

    private Integer port = 1010;

    public BootServer() {

    }

    public BootServer(int port) {
        this.port = port;
    }

    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ServerActionExecutor(socket)).start();
            }
        } catch (IOException ioExcepton) {
            ioExcepton.printStackTrace();
        }

    }

    public  static void main(String args[]) {
        new BootServer().start();
    }
}

