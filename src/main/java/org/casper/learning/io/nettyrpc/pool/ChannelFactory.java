package org.casper.learning.io.nettyrpc.pool;

import io.netty.channel.Channel;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.casper.learning.io.nettyrpc.client.RpcChannelHandler;

/**
 * @author Casper
 */
public class ChannelFactory implements KeyedPooledObjectFactory<String, RpcChannelHandler> {


    @Override
    public PooledChannel makeObject(String s) throws Exception {
        return null;
    }

    @Override
    public void destroyObject(String s, PooledObject<RpcChannelHandler> pooledObject) throws Exception {
        RpcChannelHandler channel = pooledObject.getObject();
        channel.getChannel().close();
    }

    @Override
    public boolean validateObject(String s, PooledObject<RpcChannelHandler> pooledObject) {
        RpcChannelHandler channel = pooledObject.getObject();
        if (channel.getChannel().isOpen()) {
            return true;
        }
        return false;
    }

    @Override
    public void activateObject(String s, PooledObject<RpcChannelHandler> pooledObject) throws Exception {

    }

    @Override
    public void passivateObject(String s, PooledObject<RpcChannelHandler> pooledObject) throws Exception {

    }
}
