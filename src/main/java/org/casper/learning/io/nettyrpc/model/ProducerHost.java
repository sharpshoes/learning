package org.casper.learning.io.nettyrpc.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务提供方host信息
 */
@Data
public class ProducerHost {

    private String namespace;
    private String host;
    private int port;

}
