package org.casper.learning.io.nettyrpc.client;

import org.casper.learning.io.nettyrpc.client.call.RpcEndpointClient;
import org.casper.learning.io.nettyrpc.client.pool2.RpcChannelMixedPool;
import org.casper.learning.io.nettyrpc.model.Namespace;
import org.casper.learning.io.nettyrpc.model.ServiceProvider;

import java.util.List;

/**
 * 启动器
 */
public class RpcClientBootstrap {

    public void init(List<Namespace> namespaceList) {
        namespaceList.forEach(namespace -> {
            namespace.getHosts().forEach(producerHost -> {
                RpcEndpointClient client = new RpcEndpointClient(producerHost);
                try {
                    client.start();
                    RpcChannelMixedPool.INSTANCE.register(client);
                } catch (Exception ex) {
                }
            });
        });
    }

    public void register(ServiceProvider producerHost) {
        RpcEndpointClient client = new RpcEndpointClient(producerHost);
        try {
            client.start();
            RpcChannelMixedPool.INSTANCE.register(client);
        } catch (Exception ex) {
        }
    }
}
