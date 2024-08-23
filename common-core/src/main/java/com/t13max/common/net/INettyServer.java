package com.t13max.common.net;

/**
 * netty服务器接口
 *
 * @author t13max
 * @since 18:17 2024/8/23
 */
public interface INettyServer {
    void startServer() throws InterruptedException;

    void shutdown();
}
