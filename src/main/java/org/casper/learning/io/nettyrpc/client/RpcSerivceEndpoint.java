package org.casper.learning.io.nettyrpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
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

public class RpcSerivceEndpoint {

    private EventLoopGroup group = new NioEventLoopGroup();
    private Bootstrap bootstrap = new Bootstrap();

    RpcChannelPool rpcChannelPool = new RpcChannelPool();

    private int count = 3;
    private String host;
    private int port;

    public RpcSerivceEndpoint(String host, int port) {
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
                        .addLast(new RpcChannelHandler());
                }
            });
        for (int i = 0; i < count; i++) {
            this.connect();
        }
    }

    public void connect() throws InterruptedException {
        ChannelFuture f = bootstrap.connect();
        f.addListener(new GenericFutureListener<ChannelFuture>() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    RpcChannelHandler handler = channelFuture.channel().pipeline().get(RpcChannelHandler.class);
                    rpcChannelPool.register(handler);
                }
            }
        });
    }

    public void shutdown() throws InterruptedException {
        group.shutdownGracefully().sync();
    }

    public Object call(Class<?> clazz, String method, Class<?>[] paramTypes, Object[] params) {
        RpcRequest rpcRequest = getRpcRequest(clazz, method, paramTypes, params);

        try {
            RpcResponse response = this.rpcChannelPool.call(rpcRequest);
            return response.getValue();
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private RpcRequest getRpcRequest(Class<?> clazz, String method, Class<?>[] paramTypes, Object[] params) {
        RpcRequest rpcRequest = new RpcRequest();

        rpcRequest.setClazz(clazz);
        rpcRequest.setMethod(method);
        rpcRequest.setParamTypes(paramTypes);
        rpcRequest.setParams(params);
        rpcRequest.setRequestId(UUID.randomUUID().toString());
        return rpcRequest;
    }

    public void call(Class<?> clazz, String method, Class<?>[] paramTypes, Object[] params, ServiceCallback serviceCallback) {
        RpcRequest rpcRequest = getRpcRequest(clazz, method, paramTypes, params);

        this.rpcChannelPool.call(rpcRequest, new RpcCallback() {
            @Override
            public void success(RpcRequest request, RpcResponse response) {

            }

            @Override
            public void error(RpcRequest request, Exception exception) {

            }
        });
    }
}
