package com.t13max.data.redis.utils;

import org.redisson.api.*;
import org.redisson.client.codec.Codec;

/**
 * @author t13max
 * @since 15:16 2024/10/30
 */
public interface IRedisUtils {
    
    <T> RBucket<T> getBucket(String key);

    <K, V> RMap<K, V> getMap(String key);

    <K, V> RMap<K, V> getDBMap(String key);

    <V> RSetCache<V> getSetCache(String key);

    <V> RSortedSet<V> getSortedSet(String key);

    <V> RSet<V> getSet(String key);

    <K, V> RSetMultimap<K, V> getSetMultimap(String key);

    <K, V> RSetMultimapCache<K, V> getSetMultimapCache(String key);

    <V> RList<V> getList(String key);

    <K, V> RListMultimap<K, V> getListMultimap(String key);

    <V> RQueue<V> getQueue(String key);

    <V> RBlockingQueue<V> getBlockingQueue(String key);

    <V> RDeque<V> getDeque(String key);

    RLock getLock(String key);

    RLock getMultiLock(RLock... rLock);

    RAtomicLong getAtomicLong(String key);

    RCountDownLatch getCountDownLatch(String key);

    RTopic getTopic(String key);

    RTopic getTopic(String key, Codec codec);

    <V> RGeo<V> getGeo(String key);

    <V> RScoredSortedSet<V> getScoredSortedSet(String key);

    RReadWriteLock getReadWriteLock(String key);

    RBatch getBatch(String className);

    BatchResult<?> getBatchExecute(RBatch rBatch);

    void closeRedisClient();
}
