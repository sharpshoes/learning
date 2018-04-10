package org.casper.learning.io.nettyrpc.client;

import org.casper.learning.io.nettyrpc.protocol.RpcRequest;
import org.casper.learning.io.nettyrpc.protocol.RpcResponse;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author Casper Yang
 */
public class RpcFuture implements Future<RpcResponse> {

    volatile int state = 0;
    private final int done = 1;

    private RpcRequest request;
    private RpcResponse response;
    private RpcSyncUtil sync;

    private Set<RpcCallback> callbackList = new HashSet<>();
    Lock lock = new ReentrantLock();

    public RpcFuture(RpcRequest request) {
        this.request = request;
        this.sync = new RpcSyncUtil();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDone() {
        return state == done;
    }

    @Override
    public RpcResponse get() throws InterruptedException, ExecutionException {

        if (!isDone()) {
            sync.acquire();
        }
        return this.response;
    }

    @Override
    public RpcResponse get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {

        if (!isDone()) {
            sync.acquire(timeout, unit);
        }
        if (!isDone()) {
            throw new TimeoutException();
        }
        return this.response;
    }

    public void finish(RpcResponse response) {
        this.response = response;
        this.state = done;
        sync.release();
        if (!this.callbackList.isEmpty()) {
            try {
                lock.lock();
                this.callbackList.stream().forEach(callback -> callback.success(this.request, response));
            } finally {
                lock.unlock();
            }
        }

    }

    public void addCallback(RpcCallback callback) {
        try {
            lock.lock();
            this.callbackList.add(callback);
        } finally {
            lock.unlock();
        }
    }

    public void removeCallback(RpcCallback callback) {
        try {
            lock.lock();
            this.callbackList.remove(callback);
        } finally {
            lock.unlock();
        }

    }


    public static void main(String[] args) throws InterruptedException {
        Future<RpcResponse> future = new RpcFuture(new RpcRequest());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    future.get(1000, TimeUnit.MILLISECONDS);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                ((RpcFuture) future).finish(new RpcResponse());
            }
        }).start();
    }

    public static class CallbackCall implements Callable<Void> {

        @Override
        public Void call() throws Exception {

            return null;
        }
    }


}
