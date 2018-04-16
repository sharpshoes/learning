package org.casper.learning.io.nettyrpc.client;

public interface Callback<ReturnType> {

    public void success(ReturnType value);

    public void error(Exception ex);

}
