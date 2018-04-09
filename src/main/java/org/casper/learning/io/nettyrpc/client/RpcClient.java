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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class RpcClient {

    private EventLoopGroup group = new NioEventLoopGroup();
    private Bootstrap bootstrap = new Bootstrap();

    private List<RpcClientHandler> handlerList = new ArrayList<>();
    RpcClientHandler handler;

    List<RpcClientHandler> handlerChache = new ArrayList<>();

    private int timeout = -1;
    private int count = 3;
    private String host;
    private int port;

    public RpcClient(String host, int port) {
        try {
            this.init(host, port);
        } catch (InterruptedException iEx) {
            iEx.printStackTrace();
        }
    }

    public void init(String host, int port) throws InterruptedException {

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

        this.connect();
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

    public void shutdown() throws InterruptedException {
        group.shutdownGracefully().sync();
    }

    public Object call(Class<?> clazz, String method, Class<?>[] paramTypes, Object[] params) {
        RpcRequest rpcRequest = new RpcRequest();

        rpcRequest.setClazz(clazz);
        rpcRequest.setMethod(method);
        rpcRequest.setParamTypes(paramTypes);
        rpcRequest.setParams(params);
        rpcRequest.setRequestId(UUID.randomUUID().toString());

        RpcFuture rpcFuture = handler.call(rpcRequest);
        RpcResponse response = null;
        try {
            response = rpcFuture.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return response.getValue();
    }
}
