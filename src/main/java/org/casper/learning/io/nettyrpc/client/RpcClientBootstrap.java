package org.casper.learning.io.nettyrpc.client;

import lombok.Data;
import org.casper.learning.io.nettyrpc.client.call.RpcEndpointClient;
import org.casper.learning.io.nettyrpc.client.pool2.RpcChannelMixedPool;
import org.casper.learning.io.nettyrpc.model.Namespace;
import org.casper.learning.io.nettyrpc.model.RpcEndpoint;

import java.util.List;

/**
 * 启动器
 */
@Data
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

    public void register(RpcEndpoint endpoint) {
        RpcEndpointClient client = new RpcEndpointClient(endpoint);
        try {
            client.start();
            RpcChannelMixedPool.INSTANCE.register(client);
        } catch (Exception ex) {
        }
    }

    public void unregister(RpcEndpoint endpoint) {
        RpcChannelMixedPool.INSTANCE.unregister(endpoint);
    }
}
