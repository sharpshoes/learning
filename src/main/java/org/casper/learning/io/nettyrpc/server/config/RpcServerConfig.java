package org.casper.learning.io.nettyrpc.server.config;

import lombok.Data;

@Data
public class RpcServerConfig {

    private String namespace;
    private String host;
    private int post;
}
