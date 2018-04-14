package org.casper.learning.io.nettyrpc.client.pool2;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * @author Casper
 */
public class RpcChannelSinglePool extends GenericObjectPool<PooledRpcChannel> {

    public RpcChannelSinglePool(RpcChannelFactory factory, GenericObjectPoolConfig config) {
        super(factory, config);
    }
}
