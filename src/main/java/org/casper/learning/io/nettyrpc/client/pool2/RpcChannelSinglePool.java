package org.casper.learning.io.nettyrpc.client.pool2;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * @author Casper
 */
public class RpcChannelSinglePool extends GenericObjectPool<PooledRpcChannel> {

    @Getter
    @Setter
    private String host;
    @Getter
    @Setter
    private int port;
    @Getter
    @Setter
    private String namespace;

    public RpcChannelSinglePool(RpcChannelFactory factory, GenericObjectPoolConfig config) {
        super(factory, config);
    }

    public boolean equals(Object target) {
        return false;
    }

    public int hashCode() {
        return 0;
    }

    public RpcEndpoint endpoint() {
        return RpcEndpoint.of(this.namespace, this.host, this.port);
    }

}
