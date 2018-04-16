package org.casper.learning.io.nettyrpc.client.call;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import org.casper.learning.io.nettyrpc.model.ServiceProvider;
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

    @Getter
    private String namespace;
    @Getter
    private String host;
    @Getter
    private int port;

    public RpcEndpointClient(ServiceProvider producerHost) {
        this.namespace = producerHost.getNamespace();
        this.host = producerHost.getHost();
        this.port = producerHost.getPort();
    }

    public void start() throws InterruptedException {
        this.init(this.host, this.port);
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
                        .addLast(new RpcChannelHandler(namespace, host));
                }
            });
    }

    public RpcChannelHandler connect() throws InterruptedException {
        return bootstrap.connect().sync().channel().pipeline().get(RpcChannelHandler.class);
    }


    public void shutdown() throws InterruptedException {
        group.shutdownGracefully().sync();
    }

}
