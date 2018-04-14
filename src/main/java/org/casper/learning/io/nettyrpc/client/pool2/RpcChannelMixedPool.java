package org.casper.learning.io.nettyrpc.client.pool2;

import org.casper.learning.io.nettyrpc.client.RpcCallChannel;
import org.casper.learning.io.nettyrpc.client.RpcEndpointClient;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Casper
 */
public class RpcChannelMixedPool {

    private RpcChannelMixedPool() {

    }

    public static RpcChannelMixedPool INSTANCE = new RpcChannelMixedPool();

    public Map<String, RpcChannelPoolManager> mixedPool = new HashMap<>();
    Map<String, RpcCallChannel> borrowedCache = new ConcurrentHashMap<>();

    Lock lock = new ReentrantLock();

    public void register(RpcEndpointClient endpointClient) {
        try {
            RpcChannelFactory factory = new RpcChannelFactory(endpointClient);
            RpcChannelSinglePool pool = new RpcChannelSinglePool(factory, new RpcChannelPoolConfig());
            lock.lock();
            if (!mixedPool.containsKey(endpointClient.getNamespace())) {
                mixedPool.put(endpointClient.getNamespace(), new RpcChannelPoolManager());
            }
            mixedPool.get(endpointClient).register(pool);

        } finally {
            lock.unlock();
        }
    }

//    public void unregister(RpcEndpointClient endpointClient) {
//        try {
//            lock.lock();
//            if (mixedPool.containsKey(endpointClient.getNamespace())) {
//                mixedPool.put(endpointClient.getNamespace(), new RpcChannelPoolManager());
//            }
//
//        } finally {
//            lock.unlock();
//        }
//
//    }

    public RpcCallChannel borrow(String namespace) {

        return null;
    }

    public void putBack(String namespace, RpcCallChannel rpcCallChannel) {

    }
}
