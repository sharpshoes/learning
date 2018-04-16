package org.casper.learning.io.nettyrpc.client.pool2;

import lombok.Getter;
import org.casper.learning.io.nettyrpc.client.call.RpcChannel;
import org.casper.learning.io.nettyrpc.model.RpcEndpoint;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Casper
 */
public class RpcChannelPoolManager {

    @Getter
    private List<RpcChannelSinglePool> pools = new LinkedList<>();

    private Lock lock = new ReentrantLock();
    @Getter
    private volatile boolean empty = true;

    private BorrowStrategy borrowStrategy = null;

    public void register(RpcChannelSinglePool pool) {
        try {
            lock.lock();
            empty = false;
            pools.add(pool);
        } finally {
            lock.unlock();
        }
    }

    public void unregister(RpcEndpoint rpcEndpoint) {
        for (RpcChannelSinglePool pool : pools) {
            if (pool.endpoint().equals(rpcEndpoint)) {
                this.unregister(pool);
                return;
            }
        }
    }

    private void unregister(RpcChannelSinglePool pool) {
        try {
            lock.lock();
            pools.remove(pool);
            if (this.pools.isEmpty()) {
                empty = true;
            }
        } finally {
            lock.unlock();
        }
    }

    public RpcChannel borrow() {
        try {
            if (this.empty) {
                throw new RuntimeException();
            }
            lock.lock();
            if (this.empty) {
                throw new RuntimeException();
            }
            if (this.borrowStrategy == null) {
                throw new RuntimeException();
            }
            RpcChannel channel = this.borrowStrategy.borrow();
            return channel;
        } finally {
            lock.unlock();
        }
    }

    public RpcChannel borrowNext() {
        try {
            if (this.empty) {
                throw new RuntimeException();
            }
            lock.lock();
            if (this.empty) {
                throw new RuntimeException();
            }
            if (this.borrowStrategy == null) {
                throw new RuntimeException();
            }
            RpcChannel channel = this.borrowStrategy.borrowNext();
            return channel;
        } finally {
            lock.unlock();
        }
    }

    public void giveBack(RpcChannel rpcChannel) {
        this.borrowStrategy.giveBack(rpcChannel);
    }

    public static interface BorrowStrategy {

        public RpcChannel borrow();
        /**
         * 从下一个服务列表中查找可用channel
         * @return
         */
        public RpcChannel borrowNext();

        public void giveBack(RpcChannel rpcChannel);
    }

    public static abstract class AbstractBorrowStrategy implements BorrowStrategy {
        protected List<RpcChannelSinglePool> pools;
        protected ThreadLocal<RpcChannelSinglePool> threadCurrentPool = new ThreadLocal<>();
        private ThreadLocal<Set<RpcChannelSinglePool>> threadUsedPools = new ThreadLocal<>();

        public AbstractBorrowStrategy(List<RpcChannelSinglePool> pools) {
            this.pools = pools;
        }

        @Override
        public RpcChannel borrowNext() {
            if (threadUsedPools.get() == null) {
                Set<RpcChannelSinglePool> poolSet = new HashSet<>();
                poolSet.add(threadCurrentPool.get());
                threadUsedPools.set(poolSet);
            }

            if (threadUsedPools.get().size() == this.pools.size()) {
                return null;
            }

            for (RpcChannelSinglePool pool : pools) {
                if (!threadUsedPools.get().contains(pool)) {
                    threadUsedPools.get().add(pool);
                    try {
                        return pool.borrowObject().getChannel();
                    } catch (Exception ex) {
                        throw new RuntimeException(ex.getMessage(), ex);
                    }
                }
            }
            return null;
        }

        @Override
        public void giveBack(RpcChannel rpcChannel) {
            threadCurrentPool.get().returnObject(rpcChannel.pooledChannel());
        }
    }

    public static class DefaultBorrowStrategy extends AbstractBorrowStrategy {

        private AtomicInteger count = new AtomicInteger(0);

        public DefaultBorrowStrategy(List<RpcChannelSinglePool> pools) {
            super(pools);
        }

        @Override
        public RpcChannel borrow() {

            int offset = count.getAndIncrement() % this.pools.size();
            try {

                threadCurrentPool.set(this.pools.get(offset));
                return this.pools.get(offset).borrowObject().getChannel();
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }

        }
    }
}

