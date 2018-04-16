package org.casper.learning.io.nettyrpc.pool;

import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.casper.learning.io.nettyrpc.client.call.RpcChannelHandler;

/**
 * @author Casper
 */
@Deprecated
public class PooledChannel extends DefaultPooledObject<RpcChannelHandler> {
    public PooledChannel(RpcChannelHandler channel) {
        super(channel);
    }
}
