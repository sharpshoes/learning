package org.casper.learning.io.nettyrpc.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Prc命名服务，一个命名空间，代表某一个服务器上的一组Api；
 * 同一个命名空间下的服务列表，是相同的。
 */
public class NamespaceConfig {

    private String nameSpace;
    private List<ApiConfig> apiList = new ArrayList<>();

}
