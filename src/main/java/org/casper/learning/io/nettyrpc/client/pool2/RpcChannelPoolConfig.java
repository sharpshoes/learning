package org.casper.learning.io.nettyrpc.client.pool2;

import lombok.Data;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * @author Casper
 */
@Data
public class RpcChannelPoolConfig extends GenericObjectPoolConfig {

    private int maxTotal = 3;
    private int maxIdle = 3;
    private int minIdle = 0;

}
