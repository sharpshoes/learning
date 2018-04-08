package org.casper.learning.io.bio;

import java.io.*;
import java.net.Socket;

public class ServerActionExecutor implements Runnable {

    private Socket socket = null;

    public ServerActionExecutor(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try {
            InputStream inputStream = socket.getInputStream();
            java.io.InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);

            while (true) {
                String line = bufferedReader.readLine();
                if (line.equals("end")) {
                    break;
                }
            }
            System.out.println(socket.getLocalPort());
            System.out.println(socket.getPort());
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            pw.write("Thank you\n");
            pw.write("end\n");
            pw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
