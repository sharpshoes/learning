package org.casper.learning.io.nettyrpc.client.pool2;

import lombok.Getter;
import lombok.Setter;
import org.casper.learning.io.nettyrpc.client.RpcChannel;

import java.util.UUID;

/**
 * @author Casper
 */

public class PooledRpcChannel {

    @Getter
    private RpcChannel channel;

    @Getter
    @Setter
    private String namespace;
    @Getter
    @Setter
    private String host;
    @Getter
    @Setter
    private int port;
    @Getter
    private final String identify;

    public PooledRpcChannel(RpcChannel channel) {
        this.identify = UUID.randomUUID().toString();
        this.channel = channel;

    }

    public RpcEndpoint endpoint() {
        return RpcEndpoint.of(this.namespace, this.host, this.port);
    }

}
