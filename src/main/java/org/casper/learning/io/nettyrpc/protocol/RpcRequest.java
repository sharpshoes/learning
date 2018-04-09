package org.casper.learning.io.nettyrpc.protocol;

import lombok.Data;

@Data
public class RpcRequest {

    private String requestId;
    private Class<?> clazz;
    private String method;
    private Class<?>[] paramTypes;
    private Object[] params;

}
