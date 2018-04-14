package org.casper.learning.io.nettyrpc.client.pool2;

import org.casper.learning.io.nettyrpc.client.RpcCallChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Casper
 */
public class RpcChannelPoolManager {

    private List<RpcChannelSinglePool> pools = new ArrayList<>();

    Lock lock = new ReentrantLock();

    public void register(RpcChannelSinglePool pool) {
        try {
            lock.lock();
            pools.add(pool);
        } finally {
            lock.unlock();
        }
    }

    public void unregister(RpcChannelSinglePool pool) {
        try {
            lock.lock();
            pools.remove(pool);
        } finally {
            lock.unlock();
        }
    }

    public RpcCallChannel borrow() {
        
        return null;
    }

    public void giveBack(PooledRpcChannel RpcCallChannel) {

    }

}
