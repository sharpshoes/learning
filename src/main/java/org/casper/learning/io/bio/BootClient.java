package org.casper.learning.io.bio;

public class BootClient {

    private Integer port = 1010;

    public BootClient() {

    }

    public BootClient(int port) {
        this.port = port;
    }

    public void start(int count) {
        for (int i = 0; i < count; i++) {
            new Thread(new ClientActionExecutor("casper " + i, "", port)).start();
        }
    }

    public static  void main(String args[]) {
        new BootClient().start(100);
    }
}
