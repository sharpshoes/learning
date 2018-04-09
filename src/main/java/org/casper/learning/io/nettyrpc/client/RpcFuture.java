package org.casper.learning.io.nettyrpc.client;

import org.casper.learning.io.nettyrpc.protocol.RpcRequest;
import org.casper.learning.io.nettyrpc.protocol.RpcResponse;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class RpcFuture implements Future<Object> {

    volatile int state = -1;

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
        return sync.isDone();
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        sync.acquire();
        return this.response.getValue();
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }

    public void done(RpcResponse response) {
        this.response = response;
        sync.release();
    }

    /**
     * 自定义同步器，不支持重入
     */
    static class Sync extends AbstractQueuedSynchronizer {

        private static final long serialVersionUID = 1L;

        private final int waiting = 0;
        private final int done = 1;

        public Sync() {
            this.setState(waiting);
        }

        public void acquire() {
            super.acquire(done);
        }

        public void release() {
            super.release(done);
        }

        protected boolean tryAcquire(int acquires) {
            return getState() == done;
        }

        protected boolean tryRelease(int release) {
            if (getState() == waiting) {
                if (compareAndSetState(waiting, done)) {
                    return true;
                }
            }
            return false;
        }

        public boolean isDone() {
            getState();
            return getState() == done;
        }
    }

    public static void main(String[] args) {

        RpcFuture rpcFuture = new RpcFuture(new RpcRequest());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Object obj = rpcFuture.get();
                    System.out.println("after done");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Object obj = rpcFuture.get();
                    System.out.println("after done 1");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("done");
        rpcFuture.done(new RpcResponse());
        rpcFuture.done(new RpcResponse());

//        SyncTask syncTask = new SyncTask();
//        new Thread(syncTask).start();
//        new Thread(syncTask).start();
//        new Thread(syncTask).start();
    }

    static class SyncTask implements Runnable {
        Sync sync = new Sync();
        @Override
        public void run() {
            sync.acquire();
            System.out.println(Thread.currentThread().getName() + " ");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sync.release();
        }
    }

}
