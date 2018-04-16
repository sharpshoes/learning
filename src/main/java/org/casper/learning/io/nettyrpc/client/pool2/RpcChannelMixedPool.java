package org.casper.learning.io.nettyrpc.client.pool2;

import org.casper.learning.io.nettyrpc.client.call.RpcEndpointClient;

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

    public Map<String, RpcChannelPoolManager> mixedPool = new ConcurrentHashMap<>();

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

    public void unregister(RpcEndpoint rpcEndpoint) {
        String namespace = rpcEndpoint.getNamespace();
        if (mixedPool.containsKey(namespace)) {
            poolManager(namespace).unregister(rpcEndpoint);
        }
    }

    public RpcChannelPoolManager poolManager(String namespace) {
        return this.mixedPool.get(namespace);
    }

}
