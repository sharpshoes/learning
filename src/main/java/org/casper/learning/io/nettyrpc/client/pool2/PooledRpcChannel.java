package org.casper.learning.io.nettyrpc.client.pool2;

import lombok.Getter;
import lombok.Setter;
import org.casper.learning.io.nettyrpc.client.call.RpcChannel;
import org.casper.learning.io.nettyrpc.model.RpcEndpoint;

import java.util.UUID;

/**
 * @author Casper
 */

public class PooledRpcChannel {

    @Getter
    private RpcChannel channel;

    @Setter
    private RpcEndpoint endpoint;

    @Getter
    private final String identify;

    public PooledRpcChannel(RpcChannel channel) {
        this.identify = UUID.randomUUID().toString();
        this.channel = channel;

    }

    public RpcEndpoint endpoint() {
        return this.endpoint;
    }

}
