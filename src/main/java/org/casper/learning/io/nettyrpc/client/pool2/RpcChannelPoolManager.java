package org.casper.learning.io.nettyrpc.client.pool2;

import lombok.Getter;
import org.casper.learning.io.nettyrpc.client.call.RpcChannel;

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

    private Set<RpcChannel> usedRpcCallChannelCache = new HashSet<>();

    public void register(RpcChannelSinglePool pool) {
        try {
            lock.lock();
            empty = false;
            pools.add(pool);
        } finally {
            lock.unlock();
        }
    }

    public void unregister(RpcEndpoint rpcEndpointInfo) {
        for (RpcChannelSinglePool pool : pools) {
            if (pool.endpoint().equals(rpcEndpointInfo)) {
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
            this.usedRpcCallChannelCache.add(channel);
            return channel;
        } finally {
            lock.unlock();
        }
    }

    public void giveBack(RpcChannel rpcCallChannel) {

        PooledRpcChannel pooledRpcChannel = rpcCallChannel.pooledChannel();
        for (RpcChannelSinglePool pool : pools) {
            if (pooledRpcChannel.endpoint().equals(pool.endpoint())) {
                pool.returnObject(pooledRpcChannel);
            }
        }
    }

    public static interface BorrowStrategy {
        public RpcChannel borrow();
    }

    public static abstract class AbstractBorrowStrategy implements BorrowStrategy {
        protected List<RpcChannelSinglePool> pools;
        public AbstractBorrowStrategy(List<RpcChannelSinglePool> pools) {
            this.pools = pools;
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
                return this.pools.get(offset).borrowObject().getChannel();
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }

        }
    }
}

