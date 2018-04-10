package org.casper.learning.io.nettyrpc.client;

import jdk.nashorn.internal.codegen.CompilerConstants;
import lombok.Setter;
import org.casper.learning.io.nettyrpc.protocol.RpcRequest;
import org.casper.learning.io.nettyrpc.protocol.RpcResponse;
import org.omg.CORBA.TIMEOUT;

import javax.security.auth.callback.Callback;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
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
    private Sync sync;

    private Set<Callback> callbackList = new HashSet<>();
    Lock lock = new ReentrantLock();

    public RpcFuture(RpcRequest request) {
        this.request = request;
        this.sync = new Sync();
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

    public void addCallback(Callback callback) {
        try {
            lock.lock();
            this.callbackList.add(callback);
        } finally {
            lock.unlock();
        }
    }

    public void removeCallback(Callback callback) {
        try {
            lock.lock();
            this.callbackList.remove(callback);
        } finally {
            lock.unlock();
        }

    }
    /**
     * 自定义同步器
     */
    static class Sync extends AbstractQueuedSynchronizer {

        private static final long serialVersionUID = 1L;

        private final int waiting = 0;
        private final int done = 1;

        public Sync() {
            this.setState(waiting);
        }

        public void acquire(long timeout, TimeUnit unit) throws InterruptedException {
            super.tryAcquireNanos(done, unit.toNanos(timeout));
        }

        public void acquire() {
            super.acquire(done);
        }
        public void release() {
            super.release(done);
        }

        @Override
        protected boolean tryAcquire(int acquires) {
            if(getState() == done) {
                if (compareAndSetState(done, waiting)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        protected boolean tryRelease(int release) {
            if (getState() == waiting) {
                if (compareAndSetState(waiting, done)) {
                    return true;
                }
            }
            return false;
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
    public static interface Callback {

        public void success(RpcRequest request, RpcResponse response);

        public void error(RpcRequest request, Exception exception);

    }

}
