package com.t13max.data.redis;


import com.t13max.common.config.BaseConfig;
import com.t13max.common.manager.ManagerBase;
import com.t13max.common.run.Application;
import com.t13max.data.redis.utils.IRedisUtils;
import com.t13max.data.redis.utils.RedissonUtils;
import com.t13max.data.redis.utils.SimpleRedisUtils;
import org.redisson.api.*;
import org.redisson.client.codec.Codec;

import java.util.Collections;

/**
 * @author t13max
 * @since 15:10 2024/10/30
 */
public class RedisManager extends ManagerBase {

    private IRedisUtils redisUtils;

    @Override
    protected void init() {
        BaseConfig config = Application.config();
        if (config.getRedis().isSimple()) {
            redisUtils = new SimpleRedisUtils();
        } else {
            //真正的redis 待实现
            redisUtils = new RedissonUtils(Collections.emptyList());
        }
    }

    @Override
    protected void onShutdown() {
        redisUtils.closeRedisClient();
    }

    public IRedisUtils redisUtils() {
        return redisUtils;
    }

    public <T> RBucket<T> getBucket(String key) {
        return redisUtils.getBucket(key);
    }

    public <K, V> RMap<K, V> getMap(String key) {
        return redisUtils.getMap(key);
    }

    public <K, V> RMap<K, V> getDBMap(String key) {
        return redisUtils.getDBMap(key);
    }

    public <V> RSetCache<V> getSetCache(String key) {
        return redisUtils.getSetCache(key);
    }

    public <V> RSortedSet<V> getSortedSet(String key) {
        return redisUtils.getSortedSet(key);
    }

    public <V> RSet<V> getSet(String key) {
        return redisUtils.getSet(key);
    }

    public <K, V> RSetMultimap<K, V> getSetMultimap(String key) {
        return redisUtils.getSetMultimap(key);
    }

    public <K, V> RSetMultimapCache<K, V> getSetMultimapCache(String key) {
        return redisUtils.getSetMultimapCache(key);
    }

    public <V> RList<V> getList(String key) {
        return redisUtils.getList(key);
    }

    public <K, V> RListMultimap<K, V> getListMultimap(String key) {
        return redisUtils.getListMultimap(key);
    }

    public <V> RQueue<V> getQueue(String key) {
        return redisUtils.getQueue(key);
    }

    public <V> RBlockingQueue<V> getBlockingQueue(String key) {
        return redisUtils.getBlockingQueue(key);
    }

    public <V> RDeque<V> getDeque(String key) {
        return redisUtils.getDeque(key);
    }

    public RLock getLock(String key) {
        return redisUtils.getLock(key);
    }

    public RLock getMultiLock(RLock... rLock) {
        return redisUtils.getMultiLock(rLock);
    }

    public RAtomicLong getAtomicLong(String key) {
        return redisUtils.getAtomicLong(key);
    }

    public RCountDownLatch getCountDownLatch(String key) {
        return redisUtils.getCountDownLatch(key);
    }

    public RTopic getTopic(String key) {
        return redisUtils.getTopic(key);
    }

    public RTopic getTopic(String key, Codec codec) {
        return redisUtils.getTopic(key, codec);
    }

    public <V> RGeo<V> getGeo(String key) {
        return redisUtils.getGeo(key);
    }

    public <V> RScoredSortedSet<V> getScoredSortedSet(String key) {
        return redisUtils.getScoredSortedSet(key);
    }

    public RReadWriteLock getReadWriteLock(String key) {
        return redisUtils.getReadWriteLock(key);
    }

    public RBatch getBatch(String className) {
        return redisUtils.getBatch(className);
    }

    public BatchResult<?> getBatchExecute(RBatch rBatch) {
        return redisUtils.getBatchExecute(rBatch);
    }
}
