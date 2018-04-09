package org.casper.learning.io.nettyrpc.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.casper.learning.io.nettyrpc.protocol.RpcRequest;
import org.casper.learning.io.nettyrpc.protocol.RpcResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private Channel channel = null;

    private Map<String, RpcFuture> rpcFutureMap = new java.util.concurrent.ConcurrentHashMap<>();
//    private Map<String, RpcRequest> requestMap = new ConcurrentHashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        channel = ctx.channel();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        String requestId = rpcResponse.getRequestId();

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    public RpcFuture call(RpcRequest request) {

        return new RpcFuture(request);
    }

    public RpcFuture call(RpcRequest request, int timeout) {
        return null;
    }

}
