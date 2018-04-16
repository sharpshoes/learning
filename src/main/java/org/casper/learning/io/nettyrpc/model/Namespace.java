package org.casper.learning.io.nettyrpc.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * RPC命名服务，一个命名空间，代表一组API；
 * 一个命名空间，可能被同时部署在多台服务器。
 */
@Data
public class Namespace {

    private String namespace;

    private List<RpcEndpoint> hosts = new ArrayList<>();
    private List<RpcApi> apiList = new ArrayList<>();

}
