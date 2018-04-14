package org.casper.learning.io.nettyrpc.pool;

import org.casper.learning.io.nettyrpc.client.RpcCallback;
import org.casper.learning.io.nettyrpc.client.RpcChannelHandler;
import org.casper.learning.io.nettyrpc.client.RpcFuture;
import org.casper.learning.io.nettyrpc.client.RpcSyncUtil;
import org.casper.learning.io.nettyrpc.protocol.RpcRequest;
import org.casper.learning.io.nettyrpc.protocol.RpcResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Deprecated
public class RpcChannelPool {

    private List<RpcChannelHandler> channelHandlerList = new ArrayList<>();
    private List<RpcChannelHandler> unregisterList = new ArrayList<>();

    private Lock lock = new ReentrantLock();
    private volatile int size = 0;
    RpcSyncUtil sync = new RpcSyncUtil();

    private int timeout = 60 * 1000;

    public void register(RpcChannelHandler channelHandler) {
        try {
            lock.lock();
            size += 1;
            this.channelHandlerList.add(channelHandler);
        } finally {
            sync.release();
            lock.unlock();
        }
    }

    public void unregister(RpcChannelHandler channelHandler) {
        try {
            lock.lock();
            size -= 1;
            if (channelHandlerList.contains(channelHandler)) {
                channelHandlerList.remove(channelHandler);
            } else {
                unregisterList.add(channelHandler);
            }
        } finally {
            lock.unlock();
        }
    }

    private RpcChannelHandler get() {
        if (size == 0) {
            return null;
        }

        for (;;) {
            if (!this.channelHandlerList.isEmpty()) {
                try {
                    lock.lock();
                    RpcChannelHandler channelHandler = channelHandlerList.get(0);
                    this.channelHandlerList.remove(channelHandler);
                    return channelHandler;
                } finally {
                    lock.unlock();
                }
            } else {
                // block and wait
                try {
                    sync.acquire(timeout, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    return null;
                }
            }
        }
    }

    private void putBack(RpcChannelHandler channelHandler) {
        try {
            lock.lock();
            if (this.unregisterList.contains(channelHandler)) {
                this.unregisterList.remove(channelHandler);
            } else {
                this.channelHandlerList.add(channelHandler);
            }
        } finally {
            sync.release();
            lock.unlock();
        }
    }

    public RpcResponse call(RpcRequest request) throws ExecutionException, InterruptedException {
        RpcChannelHandler channelHandler = this.get();
        try {
            RpcFuture future = channelHandler.call(request);
            return future.get();
        } finally {
            this.putBack(channelHandler);
        }
    }

    public RpcResponse call(RpcRequest request, int timeout) throws InterruptedException, ExecutionException, TimeoutException {
        RpcChannelHandler channelHandler = this.get();
        try {
            RpcFuture future = channelHandler.call(request);
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } finally {
            this.putBack(channelHandler);
        }
    }

    public void call(RpcRequest request, RpcCallback callback) {
        RpcChannelHandler channelHandler = this.get();
        try {
            channelHandler.call(request, callback);
        } finally {
            this.putBack(channelHandler);
        }
    }

    public void call(RpcRequest request, RpcCallback callback, int timeout) {
        throw new UnsupportedOperationException();
    }

    public static void main(String args[]) {
        AtomicInteger size = new AtomicInteger(0);
        System.out.println(size.incrementAndGet());
        System.out.println(size.get());
        System.out.println(size.getAndIncrement());
        System.out.println(size.get());
    }
}
