package org.casper.learning.io.nettyrpc.client;

import lombok.Setter;
import org.casper.learning.io.nettyrpc.client.annotation.RpcApi;
import org.casper.learning.io.nettyrpc.client.call.RpcChannel;
import org.casper.learning.io.nettyrpc.client.pool2.RpcChannelMixedPool;
import org.casper.learning.io.nettyrpc.client.pool2.RpcChannelPoolManager;
import org.casper.learning.io.nettyrpc.protocol.RpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.nio.channels.Channel;
import java.util.Arrays;

public class RpcServiceHandler implements InvocationHandler {

    private String namespace;
    @Setter
    private Class<?> apiClass;
    CallbackFactory callbackFactory = new CallbackFactory();

    public RpcServiceHandler(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcApi rpcApi = method.getDeclaredAnnotation(RpcApi.class);
        if (rpcApi == null) {
            throw new UnsupportedOperationException();
        }

        String apiKey = rpcApi.api();
        RpcChannelPoolManager poolManager = RpcChannelMixedPool.INSTANCE.poolManager(namespace);
        if (poolManager == null) {
            throw new UnsupportedOperationException();
        }
        RpcChannel channel = null;
        try {
            channel = poolManager.borrow();
            RpcRequest request = new RpcRequest();
            if (apiKey.equals("")) {
                request.setClazz(method.getDeclaringClass().getName());
                request.setMethod(method.getName());
            } else {
                request.setApi(apiKey);
            }

            Class<?>[] paramTypes = method.getParameterTypes();
            String[] typeStrings = new String[paramTypes.length];
            for (int i = 0; i < paramTypes.length; i++) {
                typeStrings[i] = paramTypes[i].getName();
            }
            method.getParameterTypes();


            request.setParamTypes(typeStrings);
            request.setParams(args);

        } finally {
            if (channel != null) {
                poolManager.giveBack(channel);
            }
        }

        return null;
    }

    public static void main(String args[]) {

    }
}
