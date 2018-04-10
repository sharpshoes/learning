package org.casper.learning.io.nettyrpc.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.casper.learning.io.nettyrpc.protocol.RpcRequest;
import org.casper.learning.io.nettyrpc.protocol.RpcResponse;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RpcChannelHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private Channel channel = null;

    private Map<String, RpcFuture> rpcFutureMap = new ConcurrentHashMap<>();
    private Set<HandlerCallback> callbackSet = new HashSet<>();

    Lock lock = new ReentrantLock();

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
        if (!this.callbackSet.isEmpty()) {
            try {
                lock.lock();
                this.callbackSet.stream().forEach(callback -> callback.closed(this));
            } finally {
                lock.unlock();
            }
        }
    }

    public RpcFuture call(RpcRequest request, RpcCallback callback) {

        RpcFuture rpcFuture = new RpcFuture(request);

        rpcFuture.addCallback(callback);
        this.channel.writeAndFlush(request);
        String requestId = request.getRequestId();

        this.rpcFutureMap.put(requestId, rpcFuture);
        return rpcFuture;
    }

    public RpcFuture call(RpcRequest request) {

        RpcFuture rpcFuture = new RpcFuture(request);
        this.channel.writeAndFlush(request);
        String requestId = request.getRequestId();

        this.rpcFutureMap.put(requestId, rpcFuture);
        return rpcFuture;
    }

    public void addCallback(HandlerCallback callback) {
        try {
            lock.lock();
            this.callbackSet.add(callback);
        } finally {
            lock.unlock();
        }
    }

    public void removeCallback(HandlerCallback callback) {
        if (this.callbackSet.contains(callback)) {
            try {
                lock.lock();
                this.callbackSet.remove(callback);
            } finally {
                lock.unlock();
            }
        }
    }

    public static interface HandlerCallback {

        public void closed(RpcChannelHandler handler);

    }
}
