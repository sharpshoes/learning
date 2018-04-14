package org.casper.learning.pool;

import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.PoolUtils;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;

public class ConnectionPool extends GenericKeyedObjectPool<String, Connection> {

    public ConnectionPool(KeyedPooledObjectFactory factory) {
        super(PoolUtils.synchronizedKeyedPooledFactory(factory));
    }
}
