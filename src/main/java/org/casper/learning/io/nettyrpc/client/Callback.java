package org.casper.learning.io.nettyrpc.client;


@FunctionalInterface
public interface Callback<ReturnType> {

    public void action(ReturnType value, Exception ex);

}
