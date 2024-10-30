package com.t13max.data.redis.utils;

import org.redisson.api.*;
import org.redisson.client.codec.Codec;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简易Redis实现
 *
 * @author t13max
 * @since 15:16 2024/10/30
 */
public class SimpleRedisUtils implements IRedisUtils {

    private final Map<String, String> CACHE = new ConcurrentHashMap<>();

    @Override
    public <T> RBucket<T> getBucket(String key) {
        return null;
    }

    @Override
    public <K, V> RMap<K, V> getMap(String key) {
        return null;
    }

    @Override
    public <K, V> RMap<K, V> getDBMap(String key) {
        return null;
    }

    /**
     * 集合带有超时时间
     *
     * @Author t13max
     * @Date 15:25 2024/10/30
     */
    @Override
    public <V> RSetCache<V> getSetCache(String key) {
        return null;
    }

    /**
     * 获取有序集合
     *
     * @Author t13max
     * @Date 15:25 2024/10/30
     */
    @Override
    public <V> RSortedSet<V> getSortedSet(String key) {
        return null;
    }

    /**
     * 获取集合
     *
     * @Author t13max
     * @Date 15:25 2024/10/30
     */
    @Override
    public <V> RSet<V> getSet(String key) {
        return null;
    }

    @Override
    public <K, V> RSetMultimap<K, V> getSetMultimap(String key) {
        return null;
    }

    @Override
    public <K, V> RSetMultimapCache<K, V> getSetMultimapCache(String key) {
        return null;
    }

    /**
     * 获取列表
     *
     * @Author t13max
     * @Date 15:26 2024/10/30
     */
    @Override
    public <V> RList<V> getList(String key) {
        return null;
    }

    /**
     * 获取Map
     *
     * @Author t13max
     * @Date 15:26 2024/10/30
     */
    @Override
    public <K, V> RListMultimap<K, V> getListMultimap(String key) {
        return null;
    }


    /**
     * 获取队列
     *
     * @Author t13max
     * @Date 15:26 2024/10/30
     */
    @Override
    public <V> RQueue<V> getQueue(String key) {
        return null;
    }

    /**
     * 获取阻塞队列
     *
     * @Author t13max
     * @Date 15:26 2024/10/30
     */
    @Override
    public <V> RBlockingQueue<V> getBlockingQueue(String key) {
        return null;
    }


    /**
     * 获取双端队列
     *
     * @Author t13max
     * @Date 15:26 2024/10/30
     */
    @Override
    public <V> RDeque<V> getDeque(String key) {
        return null;
    }

    /**
     * 获取锁
     *
     * @Author t13max
     * @Date 15:26 2024/10/30
     */
    @Override
    public RLock getLock(String key) {
        return null;
    }

    /**
     * 批量锁
     *
     * @Author t13max
     * @Date 15:27 2024/10/30
     */
    @Override
    public RLock getMultiLock(RLock... rLock) {
        return null;
    }

    /**
     * 获取原子数
     *
     * @Author t13max
     * @Date 15:27 2024/10/30
     */
    @Override
    public RAtomicLong getAtomicLong(String key) {
        return null;
    }

    /**
     * 获取记数锁
     *
     * @Author t13max
     * @Date 15:27 2024/10/30
     */
    @Override
    public RCountDownLatch getCountDownLatch(String key) {
        return null;
    }

    /**
     * 获取消息的Topic
     *
     * @Author t13max
     * @Date 15:28 2024/10/30
     */
    @Override
    public RTopic getTopic(String key) {
        return null;
    }

    @Override
    public RTopic getTopic(String key, Codec codec) {
        return null;
    }

    /**
     * 获取坐标
     *
     * @Author t13max
     * @Date 15:28 2024/10/30
     */
    @Override
    public <V> RGeo<V> getGeo(String key) {
        return null;
    }

    /**
     * 分数排序
     *
     * @Author t13max
     * @Date 15:28 2024/10/30
     */
    @Override
    public <V> RScoredSortedSet<V> getScoredSortedSet(String key) {
        return null;
    }

    /**
     * 读写锁
     *
     * @Author t13max
     * @Date 15:28 2024/10/30
     */
    @Override
    public RReadWriteLock getReadWriteLock(String key) {
        return null;
    }

    /**
     * 获取batch
     *
     * @Author t13max
     * @Date 15:29 2024/10/30
     */
    @Override
    public RBatch getBatch(String className) {
        return null;
    }

    /**
     * 批量执行的命令
     *
     * @Author t13max
     * @Date 15:29 2024/10/30
     */
    @Override
    public BatchResult<?> getBatchExecute(RBatch rBatch) {
        return rBatch.execute();
    }
    
    /**
     * 关闭Redisson客户端连接
     *
     * @Author t13max
     * @Date 15:29 2024/10/30
     */
    @Override
    public void closeRedisClient() {

    }

    private boolean isShuttingDown() {
        return false;
    }
}
