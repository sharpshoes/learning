package org.casper.learning.io.nettyrpc.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.casper.learning.io.nettyrpc.protocol.RpcRequest;
import org.casper.learning.io.nettyrpc.protocol.RpcResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Handler;

public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private Channel channel = null;

    private Map<String, RpcFuture> rpcFutureMap = new ConcurrentHashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        channel = ctx.channel();
        ctx.fireChannelActive();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        String requestId = rpcResponse.getRequestId();
        if (rpcFutureMap.containsKey(requestId)) {
            RpcFuture rpcFuture = rpcFutureMap.get(requestId);
            rpcFutureMap.remove(requestId);
            rpcFuture.finish(rpcResponse);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

        ctx.close();
    }

    public RpcFuture call(RpcRequest request) {

        RpcFuture rpcFuture = new RpcFuture(request);
        this.channel.writeAndFlush(request);
        String requestId = request.getRequestId();

        this.rpcFutureMap.put(requestId, rpcFuture);
        return rpcFuture;
    }

    public static interface HandlerListener {

    }
}
