package org.casper.learning.io.nettyrpc.client;

import org.casper.learning.io.nettyrpc.protocol.RpcRequest;

/**
 * @author Casper
 */
public interface RpcCallChannel {

    public RpcFuture call(RpcRequest request);
    public RpcFuture call(RpcRequest request, RpcCallback callback);

    public boolean checkValid();
    public void close();
}