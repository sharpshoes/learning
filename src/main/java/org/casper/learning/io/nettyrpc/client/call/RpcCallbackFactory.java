package org.casper.learning.io.nettyrpc.client.call;

import org.casper.learning.io.nettyrpc.client.Callback;

public class RpcCallbackFactory {

    public static RpcCallback create(Callback callback) {
        return new DefaultRpcCallback(callback);
    }
}
