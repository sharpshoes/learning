package org.casper.learning.io.nio;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class BootServer {

    private int port = 1010;

    public BootServer() {

    }

    public BootServer(int port) {
        this.port = port;
    }

    public void start() {
        try {
            ServerSocketChannel serverChannel = ServerSocketChannel.open();
            ServerSocket serverSocket = serverChannel.socket();
            Selector selector = Selector.open();
            serverSocket.bind(new InetSocketAddress(port));
            serverChannel.configureBlocking(false);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT | SelectionKey.OP_READ);
            while (true) {
                if (selector.select() > 0) {
                    selector.selectedKeys().forEach(selectionKey -> {
                        try {
                            if (selectionKey.isAcceptable()) {
                                ServerSocketChannel server = (ServerSocketChannel) selectionKey.channel();
                                SocketChannel channel = server.accept();

                            }

                            if (selectionKey.isReadable()) {
                                SocketChannel channel = (SocketChannel) selectionKey.channel();
                                this.readDataFromSocket(channel);
                            }

                            if (selectionKey.isWritable()) {

                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                    selector.selectedKeys().clear();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    protected void registerChannel(Selector selector, SelectableChannel channel, int ops) throws Exception {
        if (channel == null) {
            return;
        }
        channel.configureBlocking(false);
        channel.register(selector, ops);
    }

    private void sayHello(SocketChannel channel) throws Exception {
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
        buffer.clear();
        buffer.put("Hi there!\r\n".getBytes());
        buffer.flip();
        channel.write(buffer);
    }

    protected void readDataFromSocket(SocketChannel channel) throws Exception {
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
        int count;
        while ((count = channel.read(buffer)) > 0) {
            buffer.flip();
            channel.write(buffer);
            buffer.clear();
        }
    }

    public static void main(String args[]) {
        new BootServer().start();
    }

    public static String getString(ByteBuffer buffer) {
        Charset charset = null;
        CharsetDecoder decoder = null;
        CharBuffer charBuffer = null;
        try {
            charset = Charset.forName("UTF-8");
            decoder = charset.newDecoder();
            charBuffer = decoder.decode(buffer.asReadOnlyBuffer());
            return charBuffer.toString();

        } catch (Exception ex) {
            ex.printStackTrace();
            return "error";
        }
    }
}
