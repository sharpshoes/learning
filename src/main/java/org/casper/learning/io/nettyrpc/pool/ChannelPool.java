package org.casper.learning.io.nettyrpc.pool;

import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.casper.learning.io.nettyrpc.client.call.RpcChannelHandler;

@Deprecated
public class ChannelPool extends GenericKeyedObjectPool<String, RpcChannelHandler> {

    public ChannelPool(ChannelFactory factory) {
        super(factory);
    }

    public ChannelPool(ChannelFactory factory, GenericKeyedObjectPoolConfig poolConfig) {
        super(factory, poolConfig);
    }
}
