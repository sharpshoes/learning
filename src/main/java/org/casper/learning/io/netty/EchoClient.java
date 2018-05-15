package org.casper.learning.io.netty;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

/**
 * @author Casper
 */
public class EchoClient {

    private final String host;
    private final int port;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws Exception {

        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             .remoteAddress(new InetSocketAddress(host, port))
             .handler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel ch)
                     throws Exception {
                     ch.pipeline().addLast(
                             new EchoClientHandler());
                 }
             });

            ChannelFuture f = b.connect().sync();
            if (f.channel().isActive()) {
                f.channel().writeAndFlush(Unpooled.copiedBuffer("Hello Casper!", CharsetUtil.UTF_8));
            }

            Thread.sleep(1000);

        } finally {
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception {

        new EchoClient("127.0.0.1", 8080).start();
    }
}