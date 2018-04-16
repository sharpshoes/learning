package org.casper.learning.io.nettyrpc.client;

import jdk.nashorn.internal.codegen.CompilerConstants;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CallbackFactory {

    private Map<Class<? extends Callback>, Callback> callbackMap = new HashMap<>();
    private volatile CallbackFactoryStrategy callbackFactoryStrategy = new DefaultCallbackFactoryStrategy();
    private  Lock lock = new ReentrantLock();

    private static CallbackFactory instance = new CallbackFactory();

    private CallbackFactory() {

    }

    public static CallbackFactory factory() {
        return instance;
    }

    public static CallbackFactory factory(CallbackFactoryStrategy strategy) {
        return instance.strategy(strategy);
    }

    private CallbackFactory strategy(CallbackFactoryStrategy strategy) {
        this.callbackFactoryStrategy = strategy;
        return this;
    }

    public Callback create(Class<? extends Callback> type) {

        if (callbackMap.containsKey(type)) {
            return callbackMap.get(type);
        } else {
            try {
                lock.lock();
                Callback callback = this.callbackFactoryStrategy.create(type);
                this.callbackMap.put(type, callback);
                return callback;
            } catch (IllegalAccessException e) {
                throw new RuntimeException();
            } catch (InstantiationException e) {
                throw new RuntimeException();
            } finally {
                lock.unlock();
            }
        }
    }

    public static interface CallbackFactoryStrategy {

        public Callback create(Class<? extends Callback> type) throws IllegalAccessException, InstantiationException;
    }

    public static class DefaultCallbackFactoryStrategy implements CallbackFactoryStrategy{

        @Override
        public Callback create(Class<? extends Callback> type) throws IllegalAccessException, InstantiationException {
            return type.newInstance();
        }
    }

}
