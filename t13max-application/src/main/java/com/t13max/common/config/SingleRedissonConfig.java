package com.t13max.common.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * redisson配置
 *
 * @author t13max
 * @since 14:37 2024/10/31
 */
@Data
@Setter(AccessLevel.PRIVATE)
public class SingleRedissonConfig {
    private int idleConnectionTimeout;
    private int connectTimeout;
    private int timeout;
    private int retryAttempts;
    private int retryInterval;
    private String password;
    private int subscriptionsPerConnection;
    private String clientName;
    private String address;
    private int subscriptionConnectionMinimumIdleSize;
    private int subscriptionConnectionPoolSize;
    private int connectionMinimumIdleSize;
    private int connectionPoolSize;
    private int database;
    private int dnsMonitoringInterval;
    private int threads;
    private int nettyThreads;
    private String transportMode;

}
