package org.casper.learning.io.nettyrpc.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务提供方host信息
 */
public class ProviderHostConfig {

    private String host;
    private int port;
    private List<NamespaceConfig> namespaceConfigs = new ArrayList<>();

}
