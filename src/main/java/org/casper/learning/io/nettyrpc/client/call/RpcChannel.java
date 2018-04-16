package org.casper.learning.io.nettyrpc.client.call;

import org.casper.learning.io.nettyrpc.client.pool2.PooledRpcChannel;
import org.casper.learning.io.nettyrpc.protocol.RpcRequest;

/**
 * @author Casper
 */
public interface RpcChannel {

    public RpcFuture call(RpcRequest request);
    public RpcFuture call(RpcRequest request, RpcCallback callback);

    public boolean isWritable();
    public boolean isClosed();

    public void destroy();

    public PooledRpcChannel pooledChannel();
}