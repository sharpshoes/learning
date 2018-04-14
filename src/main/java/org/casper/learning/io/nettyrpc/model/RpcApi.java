package org.casper.learning.io.nettyrpc.model;

import lombok.Data;

@Data
public class RpcApi {

    private String apiKey;
    private String clazz;
    private String method;
    private String retType;
    private String[] paramType;
}