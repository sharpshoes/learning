package org.casper.learning.io.nettyrpc.client;

import lombok.Setter;
import org.casper.learning.io.nettyrpc.client.annotation.RpcApi;
import org.casper.learning.io.nettyrpc.client.call.RpcCallbackFactory;
import org.casper.learning.io.nettyrpc.client.call.RpcChannel;
import org.casper.learning.io.nettyrpc.client.call.RpcFuture;
import org.casper.learning.io.nettyrpc.client.pool2.RpcChannelMixedPool;
import org.casper.learning.io.nettyrpc.client.pool2.RpcChannelPoolManager;
import org.casper.learning.io.nettyrpc.protocol.RpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

public class RpcServiceHandler implements InvocationHandler {

    private String namespace;
    @Setter
    private Class<?> apiClass;
    private CallbackFactory callbackFactory = CallbackFactory.factory();

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

        RpcChannel channel = null;


        RpcRequest request = new RpcRequest();
        if (apiKey == null || "".equals(apiKey)) {
            request.setClazz(method.getDeclaringClass().getName());
            request.setMethod(method.getName());
        } else {
            request.setApi(apiKey);
        }
        Callback callback = null;

        callback = this.processParams(request, args);

        Class<? extends  Callback> callbackClass = rpcApi.callback();
        if (!callbackClass.isAssignableFrom(Callback.NoneCallback.class)) {
            callback = this.callbackFactory.create(callbackClass);
        }

        RpcChannelPoolManager poolManager = RpcChannelMixedPool.INSTANCE.poolManager(namespace);
        if (poolManager == null) {
            throw new UnsupportedOperationException();
        }

        channel = poolManager.borrow();
        while (channel.isWritable() && channel != null) {
            poolManager.giveBack(channel);
            channel = poolManager.borrowNext();
        }

        if (channel == null) {
            throw new UnsupportedOperationException();
        }
        if (callback == null) {
            RpcFuture future = channel.call(request);
            poolManager.giveBack(channel);

            return future.get();
        } else {
            channel.call(request, RpcCallbackFactory.create(callback));
            return Void.TYPE;
        }

    }

    private Callback processParams(RpcRequest request, Object[] args) {
        if (args == null || args.length == 0) {
            return null;
        }

        int lastIndex = args.length - 1;
        Callback callback = null;

        if (args[lastIndex] instanceof Callback) {
            callback = (Callback)args[lastIndex];
        } else {
            lastIndex++;
        }

        request.setParams(Arrays.copyOfRange(args, 0, lastIndex));

        Class<?>[] paramTypes = new Class<?>[lastIndex];
        for (int i = 0; i < lastIndex; i++) {
            paramTypes[i] = args[i].getClass();
        }

        return callback;
    }

    public static void main(String args[]) {
        String[] strs = new String[]{"1", "2", "3"};
        System.out.println(Arrays.copyOfRange(strs, 0, strs.length).length);
    }
}
