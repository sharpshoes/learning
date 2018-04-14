package org.casper.learning.io.nettyrpc.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;
import org.casper.learning.io.nettyrpc.protocol.RpcRequest;
import org.casper.learning.io.nettyrpc.protocol.RpcResponse;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Casper
 */
public class RpcChannelHandler extends SimpleChannelInboundHandler<RpcResponse> implements RpcCallChannel {

    @Getter
    private Channel channel = null;
    @Getter
    private String namespace;
    @Getter
    private String host;

    private Map<String, RpcFuture> rpcFutureMap = new ConcurrentHashMap<>();
    private Set<HandlerListener> handlerListeners = new HashSet<>();

    Lock lock = new ReentrantLock();

    public RpcChannelHandler(String namespace, String host) {

    }

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
        if (!this.handlerListeners.isEmpty()) {
            try {
                lock.lock();
                this.handlerListeners.stream().forEach(callback -> callback.onColse(this));
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public RpcFuture call(RpcRequest request, RpcCallback callback) {

        RpcFuture rpcFuture = new RpcFuture(request);

        rpcFuture.addCallback(callback);
        this.channel.writeAndFlush(request);
        String requestId = request.getRequestId();

        this.rpcFutureMap.put(requestId, rpcFuture);
        return rpcFuture;
    }

    @Override
    public RpcFuture call(RpcRequest request) {

        RpcFuture rpcFuture = new RpcFuture(request);
        this.channel.writeAndFlush(request);
        String requestId = request.getRequestId();

        this.rpcFutureMap.put(requestId, rpcFuture);
        return rpcFuture;
    }

    public void registerHandlerListener(HandlerListener callback) {
        try {
            lock.lock();
            this.handlerListeners.add(callback);
        } finally {
            lock.unlock();
        }
    }

    public void unregisterHandlerListener(HandlerListener callback) {
        if (this.handlerListeners.contains(callback)) {
            try {
                lock.lock();
                this.handlerListeners.remove(callback);
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public boolean checkValid() {
        return this.channel.isOpen();
    }

    @Override
    public void close() {

    }


    public static interface HandlerListener {
        public void onColse(RpcChannelHandler handler);
    }

}
