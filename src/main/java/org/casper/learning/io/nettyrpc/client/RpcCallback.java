package org.casper.learning.io.nettyrpc.client;

import org.casper.learning.io.nettyrpc.protocol.RpcRequest;
import org.casper.learning.io.nettyrpc.protocol.RpcResponse;

public interface RpcCallback {

    public void success(RpcRequest request, RpcResponse response);

    public void error(RpcRequest request, Exception exception);

}