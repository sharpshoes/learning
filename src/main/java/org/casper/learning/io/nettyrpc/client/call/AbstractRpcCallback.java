package org.casper.learning.io.nettyrpc.client.call;

import lombok.Setter;
import org.casper.learning.io.nettyrpc.client.Callback;
import org.casper.learning.io.nettyrpc.protocol.RpcRequest;
import org.casper.learning.io.nettyrpc.protocol.RpcResponse;

public abstract class AbstractRpcCallback implements RpcCallback {

    protected Callback callback;
    private boolean success = false;

    @Setter
    private RpcRequest request;
    @Setter
    private RpcResponse response;


    public AbstractRpcCallback(Callback callback) {
        this.callback = callback;
    }

    public void run() {
        if (!this.response.isError()) {
            this.success(request, response);
        } else {
            this.error(request, new Exception());
        }
    }
}
