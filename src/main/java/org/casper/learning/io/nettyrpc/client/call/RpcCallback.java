package org.casper.learning.io.nettyrpc.client.call;

import com.github.rjeschke.txtmark.Run;
import org.casper.learning.io.nettyrpc.protocol.RpcRequest;
import org.casper.learning.io.nettyrpc.protocol.RpcResponse;

import java.util.concurrent.Callable;

public interface RpcCallback extends Runnable{

    public void success(RpcRequest request, RpcResponse response);

    public void error(RpcRequest request, Exception exception);

    public void setResponse(RpcResponse response);

    public void setRequest(RpcRequest request);

}