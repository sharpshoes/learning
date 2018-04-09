package org.casper.learning.io.nettyrpc.protocol;

import lombok.Data;

@Data
public class RpcResponse {

    private String requestId;
    private int code;
    private String message;
    private Object value;

}
