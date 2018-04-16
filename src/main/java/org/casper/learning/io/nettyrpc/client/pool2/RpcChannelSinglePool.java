package org.casper.learning.io.nettyrpc.client.pool2;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.casper.learning.io.nettyrpc.model.RpcEndpoint;

/**
 * @author Casper
 */
public class RpcChannelSinglePool extends GenericObjectPool<PooledRpcChannel> {

    @Setter
    private RpcEndpoint endpoint;

    public RpcChannelSinglePool(RpcChannelFactory factory, GenericObjectPoolConfig config) {
        super(factory, config);
    }

    public RpcEndpoint endpoint() {
        return endpoint;
    }

}
