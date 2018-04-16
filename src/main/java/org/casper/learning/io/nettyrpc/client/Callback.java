package org.casper.learning.io.nettyrpc.client;


@FunctionalInterface
public interface Callback<ReturnType> {

    /**
     *
     * @param value
     * @param ex
     */
    public void action(ReturnType value, Exception ex);

    public static class NoneCallback implements Callback {
        @Override
        public void action(Object value, Exception ex) {

        }
    }

}
