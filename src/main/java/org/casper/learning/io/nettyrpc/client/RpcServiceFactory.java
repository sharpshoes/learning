package org.casper.learning.io.nettyrpc.client;

import org.casper.learning.io.nettyrpc.client.annotation.RpcService;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RpcServiceFactory {

    private Map<Class<?>, Object> rpcServiceCache = new HashMap<>();
    private Lock lock = new ReentrantLock();

    private static RpcServiceFactory instance = new RpcServiceFactory();

    public static RpcServiceFactory factory() {
        return instance;
    }

    private RpcServiceFactory() {

    }

    public <T> T lookup(Class<T> type) {
        if (rpcServiceCache.containsKey(type)) {
        return (T)rpcServiceCache.get(type);
    } else {
        try {
            lock.lock();
            RpcService rpcService = type.getDeclaredAnnotation(RpcService.class);
            if (rpcService == null) {
                throw new UnsupportedOperationException();
            }

            RpcServiceHandler serviceHandler = new RpcServiceHandler(rpcService.namespace());
            T obj = (T)Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, serviceHandler);
            this.rpcServiceCache.put(type, obj);
            return obj;
        } finally {
            lock.unlock();
        }
    }
}
}
