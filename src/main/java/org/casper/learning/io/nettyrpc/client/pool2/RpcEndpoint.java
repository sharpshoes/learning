package org.casper.learning.io.nettyrpc.client.pool2;

import lombok.Data;

@Data
public class RpcEndpoint {

    private String namespace;
    private String host;
    private int port;

    private RpcEndpoint(String namespace, String host, int port) {
        this.namespace = namespace;
        this.host = host;
        this.port = port;
    }

    public static RpcEndpoint of(String namespace, String host, int port) {
        return new RpcEndpoint(namespace, host, port);
    }

    public boolean equals(Object target) {

        if (target == null) {
            return false;
        }
        if (!(target instanceof RpcEndpoint)) {
            return false;
        }

        return this.getNamespace().equals(((RpcEndpoint) target).getNamespace())
                && this.getHost().equals(((RpcEndpoint) target).getHost())
                && this.getPort() == ((RpcEndpoint) target).getPort();

    }
}
