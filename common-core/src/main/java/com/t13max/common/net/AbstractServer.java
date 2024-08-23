package com.t13max.common.net;

import com.t13max.common.config.BaseConfig;
import com.t13max.common.config.NettyConfig;
import com.t13max.common.exception.CommonException;
import com.t13max.common.run.Application;
import com.t13max.common.run.ServerClazz;
import com.t13max.common.util.Log;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;

/**
 * 抽象Netty服务器 子类实现细节
 * 后续根据配置做精细化处理
 *
 * @author: t13max
 * @since: 18:42 2024/5/23
 */
public abstract class AbstractServer implements INettyServer {

    private ServerBootstrap bootstrap;

    private Channel channel;

    protected EventLoopGroup bossGroup;

    protected EventLoopGroup workerGroup;

    protected ChannelInitializer<SocketChannel> channelInitializer;

    @Override
    public void startServer() throws InterruptedException {

        init();

        BaseConfig config = Application.config();
        NettyConfig configNetty = config.getNetty();

        bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(configNetty.isUseEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, configNetty.getConnectTimeoutMillis())
                // 在defaults.yaml文件中，low.watermark默认大小为8388608，即8M；high.watermark默认大小为16777216，即16M
                .option(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(configNetty.getLowWaterMark(), configNetty.getHighWaterMark()))
                .option(ChannelOption.SO_BACKLOG, configNetty.getSoBackLog())
                .option(ChannelOption.SO_REUSEADDR, configNetty.isSsoReuseAddr())
                .childOption(ChannelOption.TCP_NODELAY, configNetty.isTcpNodelay())
                .childOption(ChannelOption.SO_KEEPALIVE, configNetty.isSsoKeepAlive())
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

        bootstrap.childHandler(channelInitializer);

        InetSocketAddress address = new InetSocketAddress(configNetty.getPort());
        channel = bootstrap.bind(address).sync().channel();
        Log.msg.info("netty server bind on {} success!", address);

    }

    /**
     * 关闭
     *
     * @Author t13max
     * @Date 19:12 2024/5/23
     */
    @Override
    public void shutdown() {
        try {

            Log.msg.info("netty shutdown begin!");

            if (null != channel) {
                ChannelFuture f = channel.close();
                f.awaitUninterruptibly();
            }

        } finally {
            if (this.workerGroup != null) {
                this.workerGroup.shutdownGracefully();
            }
            if (this.bossGroup != null) {
                this.bossGroup.shutdownGracefully();
            }
        }

        Log.msg.info("NettyServer shutdown success!");
    }

    /**
     * 初始化各个组件
     *
     * @Author t13max
     * @Date 19:24 2024/5/23
     */
    protected abstract void init();

    /**
     * 初始化server对象
     *
     * @Author t13max
     * @Date 18:23 2024/8/23
     */
    public static void initServer(Class<?> clazz) throws RuntimeException {

        ServerClazz annotation = clazz.getAnnotation(ServerClazz.class);

        if (annotation == null) {
            Log.msg.info("不启动监听...");
            return;
        }

        Class<? extends INettyServer> serverClazz = annotation.serverClazz();
        if (serverClazz == null) {
            Log.msg.info("不启动监听...");
            return;
        }

        Constructor<? extends INettyServer> constructor;

        try {
            constructor = serverClazz.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new CommonException("无法启动监听, 没有空参构造");
        }

        INettyServer baseServer;
        try {
            baseServer = constructor.newInstance();
        } catch (Exception e) {
            throw new CommonException("无法启动监听, 创建server对象失败, error=" + e.getMessage());
        }

        try {
            //开启监听
            baseServer.startServer();
            //停服
            Application.addShutdownHook(baseServer::shutdown);
        } catch (InterruptedException e) {
            throw new CommonException("监听服被打断");
        }
    }
}
