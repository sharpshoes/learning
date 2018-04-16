package org.casper.learning.io.nettyrpc.client.pool2;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.casper.learning.io.nettyrpc.client.RpcChannel;
import org.casper.learning.io.nettyrpc.client.RpcChannelHandler;
import org.casper.learning.io.nettyrpc.client.RpcEndpointClient;

/**
 * @author Casper
 */
public class RpcChannelFactory implements PooledObjectFactory<PooledRpcChannel> {

    private RpcEndpointClient endpointClient;

    public RpcChannelFactory(RpcEndpointClient endpointClient) {
        this.endpointClient = endpointClient;
    }

    @Override
    public PooledObject<PooledRpcChannel> makeObject() throws Exception {

        RpcChannelHandler channelHandler = this.endpointClient.connect();
        PooledRpcChannel pooledRpcChannel = new PooledRpcChannel(channelHandler);

        channelHandler.setPooledChannel(pooledRpcChannel);

        pooledRpcChannel.setNamespace(endpointClient.getNamespace());
        pooledRpcChannel.setHost(endpointClient.getHost());
        pooledRpcChannel.setPort(endpointClient.getPort());

        return new DefaultPooledObject(pooledRpcChannel);
    }

    @Override
    public void destroyObject(PooledObject<PooledRpcChannel> pooledObject) throws Exception {
        RpcChannel channelHandler = pooledObject.getObject().getChannel();
        channelHandler.close();
    }

    @Override
    public boolean validateObject(PooledObject<PooledRpcChannel> pooledObject) {
        RpcChannel channelHandler = pooledObject.getObject().getChannel();
        return channelHandler.checkValid();
    }

    @Override
    public void activateObject(PooledObject<PooledRpcChannel> pooledObject) throws Exception {

    }

    @Override
    public void passivateObject(PooledObject<PooledRpcChannel> pooledObject) throws Exception {

    }
}
