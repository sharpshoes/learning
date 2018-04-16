package org.casper.learning.io.nettyrpc.client.call;

import org.casper.learning.io.nettyrpc.client.Callback;
import org.casper.learning.io.nettyrpc.protocol.RpcRequest;
import org.casper.learning.io.nettyrpc.protocol.RpcResponse;

public class DefaultRpcCallback extends AbstractRpcCallback {

    public DefaultRpcCallback(Callback callback) {
        super(callback);
    }

    @Override
    public void success(RpcRequest request, RpcResponse response) {
        if (callback != null) {
            callback.success(response.getValue());
        }
    }

    @Override
    public void error(RpcRequest request, Exception exception) {
        if (callback != null) {
            callback.error(exception);
        }
    }
}
