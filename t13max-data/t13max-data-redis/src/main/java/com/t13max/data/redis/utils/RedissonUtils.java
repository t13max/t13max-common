package com.t13max.data.redis.utils;

import org.redisson.api.*;
import org.redisson.client.codec.Codec;

import java.util.List;

/**
 * Redisson实现
 *
 * @author t13max
 * @since 15:16 2024/10/30
 */
public class RedissonUtils implements IRedisUtils {

    private final List<RedissonClient> redissonClientList;

    public RedissonUtils(List<RedissonClient> redissonClientList) {
        this.redissonClientList = redissonClientList;
    }

    public int allocateExecutiveCell(long aid) {
        return (int) (Math.abs(aid) % redissonClientList.size());
    }

    @Override
    public <T> RBucket<T> getBucket(String key) {
        RedissonClient redissonClient = redissonClientList.get(allocateExecutiveCell(key.hashCode()));
        return redissonClient.getBucket(key);
    }

    @Override
    public <K, V> RMap<K, V> getMap(String key) {
        RedissonClient redissonClient = redissonClientList.get(allocateExecutiveCell(key.hashCode()));
        return redissonClient.getMap(key);
    }

    @Override
    public <K, V> RMap<K, V> getDBMap(String key) {
        RedissonClient redissonClient = redissonClientList.get(allocateExecutiveCell(key.hashCode()));
        return redissonClient.getMap(key);
    }

    /**
     * 集合带有超时时间
     *
     * @Author t13max
     * @Date 15:25 2024/10/30
     */
    @Override
    public <V> RSetCache<V> getSetCache(String key) {

        RedissonClient redissonClient = redissonClientList.get(allocateExecutiveCell(key.hashCode()));
        return redissonClient.getSetCache(key);
    }

    /**
     * 获取有序集合
     *
     * @Author t13max
     * @Date 15:25 2024/10/30
     */
    @Override
    public <V> RSortedSet<V> getSortedSet(String key) {
        RedissonClient redissonClient = redissonClientList.get(allocateExecutiveCell(key.hashCode()));
        return redissonClient.getSortedSet(key);
    }

    /**
     * 获取集合
     *
     * @Author t13max
     * @Date 15:25 2024/10/30
     */
    @Override
    public <V> RSet<V> getSet(String key) {
        RedissonClient redissonClient = redissonClientList.get(allocateExecutiveCell(key.hashCode()));
        return redissonClient.getSet(key);
    }

    @Override
    public <K, V> RSetMultimap<K, V> getSetMultimap(String key) {
        RedissonClient redissonClient = redissonClientList.get(allocateExecutiveCell(key.hashCode()));
        return redissonClient.getSetMultimap(key);
    }

    @Override
    public <K, V> RSetMultimapCache<K, V> getSetMultimapCache(String key) {
        RedissonClient redissonClient = redissonClientList.get(allocateExecutiveCell(key.hashCode()));
        return redissonClient.getSetMultimapCache(key);
    }

    /**
     * 获取列表
     *
     * @Author t13max
     * @Date 15:26 2024/10/30
     */
    @Override
    public <V> RList<V> getList(String key) {
        RedissonClient redissonClient = redissonClientList.get(allocateExecutiveCell(key.hashCode()));
        return redissonClient.getList(key);
    }

    /**
     * 获取Map
     *
     * @Author t13max
     * @Date 15:26 2024/10/30
     */
    @Override
    public <K, V> RListMultimap<K, V> getListMultimap(String key) {
        RedissonClient redissonClient = redissonClientList.get(allocateExecutiveCell(key.hashCode()));
        return redissonClient.getListMultimap(key);
    }


    /**
     * 获取队列
     *
     * @Author t13max
     * @Date 15:26 2024/10/30
     */
    @Override
    public <V> RQueue<V> getQueue(String key) {
        RedissonClient redissonClient = redissonClientList.get(allocateExecutiveCell(key.hashCode()));
        return redissonClient.getQueue(key);
    }

    /**
     * 获取阻塞队列
     *
     * @Author t13max
     * @Date 15:26 2024/10/30
     */
    @Override
    public <V> RBlockingQueue<V> getBlockingQueue(String key) {
        RedissonClient redissonClient = redissonClientList.get(allocateExecutiveCell(key.hashCode()));
        return redissonClient.getBlockingQueue(key);
    }


    /**
     * 获取双端队列
     *
     * @Author t13max
     * @Date 15:26 2024/10/30
     */
    @Override
    public <V> RDeque<V> getDeque(String key) {
        RedissonClient redissonClient = redissonClientList.get(allocateExecutiveCell(key.hashCode()));
        return redissonClient.getDeque(key);
    }

    /**
     * 获取锁
     *
     * @Author t13max
     * @Date 15:26 2024/10/30
     */
    @Override
    public RLock getLock(String key) {

        RedissonClient redissonClient = redissonClientList.get(allocateExecutiveCell(key.hashCode()));
        return redissonClient.getLock(key);
    }

    /**
     * 批量锁
     *
     * @Author t13max
     * @Date 15:27 2024/10/30
     */
    @Override
    public RLock getMultiLock(RLock... rLock) {
        RedissonClient redissonClient = redissonClientList.get(0);
        return redissonClient.getMultiLock(rLock);
    }

    /**
     * 获取原子数
     *
     * @Author t13max
     * @Date 15:27 2024/10/30
     */
    @Override
    public RAtomicLong getAtomicLong(String key) {
        RedissonClient redissonClient = redissonClientList.get(allocateExecutiveCell(key.hashCode()));
        return redissonClient.getAtomicLong(key);
    }

    /**
     * 获取记数锁
     *
     * @Author t13max
     * @Date 15:27 2024/10/30
     */
    @Override
    public RCountDownLatch getCountDownLatch(String key) {
        RedissonClient redissonClient = redissonClientList.get(allocateExecutiveCell(key.hashCode()));
        return redissonClient.getCountDownLatch(key);
    }

    /**
     * 获取消息的Topic
     *
     * @Author t13max
     * @Date 15:28 2024/10/30
     */
    @Override
    public RTopic getTopic(String key) {
        RedissonClient redissonClient = redissonClientList.get(allocateExecutiveCell(key.hashCode()));
        return redissonClient.getTopic(key);
    }

    @Override
    public RTopic getTopic(String key, Codec codec) {
        RedissonClient redissonClient = redissonClientList.get(allocateExecutiveCell(key.hashCode()));
        return redissonClient.getTopic(key);
    }

    /**
     * 获取坐标
     *
     * @Author t13max
     * @Date 15:28 2024/10/30
     */
    @Override
    public <V> RGeo<V> getGeo(String key) {
        RedissonClient redissonClient = redissonClientList.get(allocateExecutiveCell(key.hashCode()));
        return redissonClient.getGeo(key);
    }

    /**
     * 分数排序
     *
     * @Author t13max
     * @Date 15:28 2024/10/30
     */
    @Override
    public <V> RScoredSortedSet<V> getScoredSortedSet(String key) {
        RedissonClient redissonClient = redissonClientList.get(allocateExecutiveCell(key.hashCode()));
        return redissonClient.getScoredSortedSet(key);
    }

    /**
     * 读写锁
     *
     * @Author t13max
     * @Date 15:28 2024/10/30
     */
    @Override
    public RReadWriteLock getReadWriteLock(String key) {
        RedissonClient redissonClient = redissonClientList.get(allocateExecutiveCell(key.hashCode()));
        return redissonClient.getReadWriteLock(key);
    }

    /**
     * 获取batch
     *
     * @Author t13max
     * @Date 15:29 2024/10/30
     */
    @Override
    public RBatch getBatch(String className) {
        RedissonClient redissonClient = redissonClientList.get(allocateExecutiveCell(className.hashCode()));
        return redissonClient.createBatch();
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
        redissonClientList.forEach(RedissonClient::shutdown);
    }

    private boolean isShuttingDown() {
        return redissonClientList.stream().anyMatch(RedissonClient::isShuttingDown);
    }

}
