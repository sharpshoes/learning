package org.casper.learning.io.nettyrpc.client.call;

import org.casper.learning.io.nettyrpc.client.RpcSyncUtil;
import org.casper.learning.io.nettyrpc.client.call.RpcCallback;
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

    private volatile int state = 0;
    private final int done = 1;

    private RpcRequest request;
    private RpcResponse response;
    private RpcSyncUtil sync;
    private ExecutorService executorPool;

    private Set<RpcCallback> callbackList = new HashSet<>();
    Lock lock = new ReentrantLock();

    private RpcFuture(RpcRequest request, ExecutorService executorPool) {
        this.request = request;
        this.executorPool = executorPool;
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

            this.callbackList.stream().forEach(callback -> {
                callback.setRequest(request);
                callback.setResponse(response);
                executorPool.submit(callback);
            });
        }
    }

    public void error(Exception ex) {

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


    public static class CallbackCall implements Callable<Void> {

        @Override
        public Void call() throws Exception {

            return null;
        }
    }

    public static RpcFuture create(RpcRequest request) {
        return RpcFutureFactory.create(request);
    }

    private static class RpcFutureFactory {

        private static ExecutorService executorPool = new ThreadPoolExecutor(2, 4, 1000, TimeUnit.HOURS, new LinkedBlockingQueue<>());

        public static RpcFuture create(RpcRequest request) {
            return new RpcFuture(request, executorPool);
        }
    }

}
