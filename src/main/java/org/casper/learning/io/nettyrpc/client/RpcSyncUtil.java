package org.casper.learning.io.nettyrpc.client;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

    /**
     * 自定义同步器
     */
    class RpcSyncUtil extends AbstractQueuedSynchronizer {

        private static final long serialVersionUID = 1L;

        private final int waiting = 0;
        private final int done = 1;

        public RpcSyncUtil() {
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