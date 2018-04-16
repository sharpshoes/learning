package org.casper.learning.io.nettyrpc.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务提供方host信息
 */
public class RpcEndpoint {

    @Getter
    private String namespace;
    @Getter
    private String host;
    @Getter
    private int port;
    @Getter
    @Setter
    private int weight;

    private RpcEndpoint(String namespace, String host, int port) {
        this.namespace = namespace;
        this.host = host;
        this.port = port;
    }

    public static RpcEndpoint of(String namespace, String host, int port) {
        return new RpcEndpoint(namespace, host, port);
    }

    @Override
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

    @Override
    public int hashCode() {
        return (this.getNamespace().hashCode() * 31
                + this.host.hashCode()) * 31
                + this.getPort();
    }
}
