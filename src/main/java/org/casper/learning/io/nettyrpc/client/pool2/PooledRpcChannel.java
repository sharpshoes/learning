package org.casper.learning.io.nettyrpc.client.pool2;

import lombok.Data;
import org.casper.learning.io.nettyrpc.client.RpcCallChannel;

/**
 * @author Casper
 */
@Data
public class PooledRpcChannel {

    private RpcCallChannel channel;

    private String namespace;
    private String host;
    private int port;

    public PooledRpcChannel(RpcCallChannel channel) {
        this.channel = channel;
    }

}
