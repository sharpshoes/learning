package org.casper.learning.io.nettyrpc.protocol;

import lombok.Data;

import java.util.UUID;

@Data
public class RpcRequest {

    public RpcRequest() {
        this.requestId = UUID.randomUUID().toString();
    }

    private String requestId;

    private String api;

    private String clazz;
    private String method;

    private String[] paramTypes;
    private Object[] params;
    private String returnType;
    private String Exception;

}
