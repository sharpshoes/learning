package org.casper.learning.io.nettyrpc.pool;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectState;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.casper.learning.io.nettyrpc.client.RpcChannelHandler;

import java.io.PrintWriter;
import java.util.Deque;

/**
 * @author Casper
 */
@Deprecated
public class PooledChannel extends DefaultPooledObject<RpcChannelHandler> {
    public PooledChannel(RpcChannelHandler channel) {
        super(channel);
    }
}
