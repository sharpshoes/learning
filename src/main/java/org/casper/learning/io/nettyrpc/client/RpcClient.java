package org.casper.learning.io.nettyrpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.casper.learning.io.nettyrpc.protocol.RpcDecoder;
import org.casper.learning.io.nettyrpc.protocol.RpcEncoder;
import org.casper.learning.io.nettyrpc.protocol.RpcRequest;
import org.casper.learning.io.nettyrpc.protocol.RpcResponse;

import java.net.InetSocketAddress;

public class RpcClient {

    EventLoopGroup group = new NioEventLoopGroup();
    Bootstrap bootstrap = new Bootstrap();

    public RpcClient(String host, int port) {

    }

    public void init(String host, int port) {

        bootstrap.group(group)
            .channel(NioSocketChannel.class)
            .remoteAddress(new InetSocketAddress(host, port))
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch)
                        throws Exception {
                    ch.pipeline()
                        .addLast(new RpcEncoder(RpcRequest.class))
                        .addLast(new RpcDecoder(RpcResponse.class))
                        .addLast(new RpcClientHandler());
                }
            });
    }

    public Channel connect() throws InterruptedException {

        ChannelFuture f = bootstrap.connect().sync();
        return f.channel();
    }

    public void shutdown() throws InterruptedException {

        group.shutdownGracefully().sync();
    }


}
