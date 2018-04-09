package org.casper.learning.io.nettyrpc.client;

import lombok.Setter;
import org.casper.learning.io.nettyrpc.protocol.RpcRequest;
import org.casper.learning.io.nettyrpc.protocol.RpcResponse;
import org.omg.CORBA.TIMEOUT;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * @Author Casper Yang
 */
public class RpcFuture implements Future<RpcResponse> {

    volatile int state = 0;
    private final int done = 1;

    private RpcRequest request;
    private RpcResponse response;
    private Sync sync;

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

    static class SyncTask implements Runnable {
        Sync sync = new Sync();
        @Override
        public void run() {
            sync.acquire();
            System.out.println(Thread.currentThread().getName() + " ");
        }

        public void done() {
            sync.release();
        }
    }

}
