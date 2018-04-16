package org.casper.learning.io.nettyrpc.protocol;

import lombok.Data;

@Data
public class RpcResponse {

    public static final int ERROR_CODE = -1;

    private String requestId;
    private int code;
    private String message;
    private Object value;

    public boolean isError() {
        return ERROR_CODE == code;
    }
}
