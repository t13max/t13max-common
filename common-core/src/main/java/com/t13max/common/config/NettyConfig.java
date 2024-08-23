package com.t13max.common.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: t13max
 * @since: 19:09 2024/5/23
 */
@Getter
@Setter(AccessLevel.PRIVATE)
public class NettyConfig {

    //使用epoll
    protected boolean useEpoll;

    //对外端口
    protected int port;

    private int ringBufferSize = 64 * 1024;
    private int tcpPort = 6000;
    private int connectTimeoutMillis = 3000;                // 连接超时
    private int lowWaterMark = 128*1024;                // 低水位
    private int highWaterMark = 256*1024;               // 高水位
    private int soBackLog = 1024;                       //
    private boolean ssoReuseAddr = true;                 //
    private boolean tcpNodelay = true;                  //
    private boolean ssoKeepAlive = true;                 //
    private long idleTimeOutMillis = 5 * 60 * 1000;     // 空闲超时时间设定
    private int idleScheduleInitial = 10000;              // 10秒
    private int idleSchedulePeriod = 60000;               // 60秒
    private int reconnectDelayMillis = 3000;                // 重连延时3秒
    private boolean csoKeepAlive = true;                 //
    private boolean csoReuseAddr = true;                 //
    private long readerIdleTime = 0;                    // 秒
    private long writerIdleTime = 0;                    // 秒
    private long clientAllIdleTime = 10000;             // 客户端10秒
    private long serverAllIdleTime = 60000;             // 服务器60秒
    private int maxFrameLength = 1024*1024;             //最大包长度
}
