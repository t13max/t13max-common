package com.t13max.common.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author t13max
 * @since 17:06 2024/9/11
 */
@Getter
@Setter(AccessLevel.PRIVATE)
public class RpcConfig {

    private boolean open;

    private boolean failsafe = true;

    private boolean rateLimit = true;

    private boolean inJVM = true;

    private String registryProtocol = "zookeeper";

    private String address = "127.0.0.1:2181";

    private int callTimeout = 60000;

    private int methodConcurrents = 32;

    private int cores = 8;

}
