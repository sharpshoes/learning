package org.casper.learning.io.bio;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.rmi.server.ExportException;

public class ClientActionExecutor implements Runnable {

    private String name;
    private Integer port;
    private String address;

    public  ClientActionExecutor(String name, String serverAddress, int port) {
        this.name = name;
        this.address = serverAddress;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(InetAddress.getLocalHost(),port);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write("I am " + name + "\n");
            writer.write("end\n");
            writer.flush();
            while (true) {
                socket.getInputStream().read();
                String line = reader.readLine();
                System.out.println(line);
                if ("end".equals(line)) {
                    break;
                }
            }

            System.out.println(socket.getLocalPort());
            System.out.println(socket.getPort());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
