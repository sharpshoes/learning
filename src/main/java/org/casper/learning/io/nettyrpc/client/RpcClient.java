package org.casper.learning.io.nettyrpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.casper.learning.io.nettyrpc.protocol.RpcDecoder;
import org.casper.learning.io.nettyrpc.protocol.RpcEncoder;
import org.casper.learning.io.nettyrpc.protocol.RpcRequest;
import org.casper.learning.io.nettyrpc.protocol.RpcResponse;

import java.net.InetSocketAddress;

public class RpcClient {

    EventLoopGroup group = new NioEventLoopGroup();
    Bootstrap bootstrap = new Bootstrap();
    RpcClientHandler handler = new RpcClientHandler();

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

    public void connect() throws InterruptedException {

        ChannelFuture f = bootstrap.connect();
        f.addListener(new GenericFutureListener<ChannelFuture>() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    RpcClientHandler handler = channelFuture.channel().pipeline().get(RpcClientHandler.class);
                }
            }
        });
    }

    private void addHandler(RpcClientHandler handler) {

//        connectedHandlers.add(handler);
//        InetSocketAddress remoteAddress = (InetSocketAddress) handler.getChannel().remoteAddress();
//        connectedServerNodes.put(remoteAddress, handler);
//        signalAvailableHandler();
    }

    public void shutdown() throws InterruptedException {

        group.shutdownGracefully().sync();
    }


}
