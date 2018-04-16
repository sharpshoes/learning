package org.casper.learning.io.nettyrpc.client.call;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.Setter;
import org.casper.learning.io.nettyrpc.model.RpcEndpoint;
import org.casper.learning.io.nettyrpc.protocol.RpcDecoder;
import org.casper.learning.io.nettyrpc.protocol.RpcEncoder;
import org.casper.learning.io.nettyrpc.protocol.RpcRequest;
import org.casper.learning.io.nettyrpc.protocol.RpcResponse;

import java.net.InetSocketAddress;

/**
 * @Author: Casper
 */
public class RpcEndpointClient {

    private EventLoopGroup group = new NioEventLoopGroup();
    private Bootstrap bootstrap = new Bootstrap();

    private RpcEndpoint endpoint;

    public RpcEndpointClient(RpcEndpoint rpcEndpoint) {
        this.endpoint = rpcEndpoint;
    }

    public void start() throws InterruptedException {
        this.init(endpoint.getHost(), this.endpoint.getPort());
    }

    private void init(String host, int port) throws InterruptedException {
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
    }

    public RpcChannelHandler connect() throws InterruptedException {
        return bootstrap.connect().sync().channel().pipeline().get(RpcChannelHandler.class);
    }

    public RpcEndpoint endpoint() {
        return this.endpoint;
    }

    public void shutdown() throws InterruptedException {
        group.shutdownGracefully().sync();
    }

}
