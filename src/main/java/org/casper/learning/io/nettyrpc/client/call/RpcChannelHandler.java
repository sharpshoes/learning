package org.casper.learning.io.nettyrpc.client.call;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;
import lombok.Setter;
import org.casper.learning.io.nettyrpc.client.pool2.PooledRpcChannel;
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
public class RpcChannelHandler extends SimpleChannelInboundHandler<RpcResponse> implements RpcChannel {

    @Getter
    private Channel channel = null;
    @Setter
    private PooledRpcChannel pooledChannel;

    private Map<String, RpcFuture> rpcFutureMap = new ConcurrentHashMap<>();
    private Set<RpcChannelListener> rpcChannelListeners = new HashSet<>();

    Lock lock = new ReentrantLock();

    public RpcChannelHandler() {

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
        if (!this.rpcChannelListeners.isEmpty()) {
            try {
                lock.lock();
                this.rpcChannelListeners.stream().forEach(callback -> callback.onDestroy(this));
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public RpcFuture call(RpcRequest request, RpcCallback callback) {

        RpcFuture rpcFuture = RpcFuture.create(request);

        rpcFuture.addCallback(callback);
        this.channel.writeAndFlush(request);
        String requestId = request.getRequestId();

        this.rpcFutureMap.put(requestId, rpcFuture);
        return rpcFuture;
    }

    @Override
    public RpcFuture call(RpcRequest request) {

        RpcFuture rpcFuture = RpcFuture.create(request);
        this.channel.writeAndFlush(request);
        String requestId = request.getRequestId();

        this.rpcFutureMap.put(requestId, rpcFuture);
        return rpcFuture;
    }

    public void registerHandlerListener(RpcChannelListener callback) {
        try {
            lock.lock();
            this.rpcChannelListeners.add(callback);
        } finally {
            lock.unlock();
        }
    }

    public void unregisterHandlerListener(RpcChannelListener callback) {
        if (this.rpcChannelListeners.contains(callback)) {
            try {
                lock.lock();
                this.rpcChannelListeners.remove(callback);
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public boolean isWritable() {
        return this.channel.isActive() && this.channel.isWritable();
    }

    @Override
    public boolean isClosed() {
        return !channel.isActive();
    }

    @Override
    public void destroy() {
        for (RpcFuture future : this.rpcFutureMap.values()) {
            future.error(new Exception());
        }
        this.rpcFutureMap = null;

        for (RpcChannelListener listener : this.rpcChannelListeners) {
            listener.onDestroy(this);
        }
    }

    @Override
    public PooledRpcChannel pooledChannel() {
        return pooledChannel;
    }


    public static interface RpcChannelListener {
        public void onDestroy(RpcChannel handler);
    }

}
