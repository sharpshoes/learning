package org.casper.learning.thread;

public class Producer implements  Runnable {

    volatile int status = 1;
    @Override
    public void run() {
        while (true) {
            if (status == 1) {
                System.out.println("Running");
                status = 2;
            } else if (status == 2) {
                System.out.println("Stopping");
                status = 1;
            }
        }
    }

    public static void main(String args[]) {

    }
}
